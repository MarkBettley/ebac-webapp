package com.ebac.modulo45.controller;

import com.ebac.modulo45.dto.ResponseWrapper;
import com.ebac.modulo45.dto.Telefono;
import com.ebac.modulo45.dto.Usuario;
import com.ebac.modulo45.feign.FeignUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
public class UsuarioController {

    @Autowired
    FeignUserService feignUserService;

    @RequestMapping(value = "/usuario", method = RequestMethod.GET)
    public Object informacionUsuario(HttpServletRequest request,
                                     HttpServletResponse response,
                                     Model model) {

        String idUsuario = request.getParameter("idUsuario");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("usuario");

        Usuario usuario = Usuario.creaUsuarioVacio();

        if (!Objects.isNull(idUsuario) && !idUsuario.isEmpty()) {
            try {
                ResponseWrapper<Usuario> usuarioResponse =
                        feignUserService.getUserById(Integer.parseInt(idUsuario));

                if (usuarioResponse != null && usuarioResponse.isSuccess()) {
                    usuario = usuarioResponse.getResponseEntity().getBody();
                }
            } catch (NumberFormatException e) {
                log.warn("Id de usuario invalido: {}", idUsuario);
            }
        }

        model.addAttribute("usuario", usuario);
        return modelAndView;
    }

    @RequestMapping(value = "/formulario-usuario", method = RequestMethod.GET)
    public Object formularioUsuario(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Model model) {

        String idUsuario = request.getParameter("idUsuario");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("formulario-usuario");

        Usuario usuario = Usuario.creaUsuarioVacio();
        List<Telefono> telefonos = new ArrayList<>(usuario.getTelefonos());

        model.addAttribute("propositoFormulario", "Crear usuario");

        if (!Objects.isNull(idUsuario) && !idUsuario.isEmpty()) {
            try {
                ResponseWrapper<Usuario> usuarioResponse =
                        feignUserService.getUserById(Integer.parseInt(idUsuario));

                if (usuarioResponse != null
                        && usuarioResponse.isSuccess()
                        && usuarioResponse.getResponseEntity() != null
                        && usuarioResponse.getResponseEntity().getBody() != null) {

                    usuario = usuarioResponse.getResponseEntity().getBody();

                    if (usuario.getTelefonos() != null
                            && !usuario.getTelefonos().isEmpty()) {
                        telefonos = usuario.getTelefonos();
                    } else {
                        telefonos = new ArrayList<>();
                        telefonos.add(Telefono.builder()
                                .tipoTelefono("")
                                .lada(0)
                                .numero("")
                                .build());
                    }

                    model.addAttribute(
                            "propositoFormulario",
                            "Actualizar usuario");
                }
            } catch (NumberFormatException e) {
                log.warn("Id de usuario invalido: {}", idUsuario);
            }
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("telefonos", telefonos);

        return modelAndView;
    }

    @RequestMapping(value = "/guardar-usuario", method = RequestMethod.POST)
    public ResponseEntity<Response> saveUserConfiguration(
            HttpServletRequest request) {

        HttpStatus statusCode = HttpStatus.OK;
        Response response = null;

        try {
            String usuarioId = request.getParameter("FormUsuarioId");
            String usuarioNombre = request.getParameter("FormUsuarioNombre");
            String usuarioEdad = request.getParameter("FormUsuarioEdad");

            String[] telefonoTipos =
                    request.getParameterValues("FormTelefonoTipo");
            String[] telefonoLadas =
                    request.getParameterValues("FormTelefonoLada");
            String[] telefonoNumeros =
                    request.getParameterValues("FormTelefonoNumero");

            ErrorResponse errores = new ErrorResponse();

            if (usuarioNombre == null || usuarioNombre.isBlank()) {
                errores.addMessage("Nombre");
            }

            int edad = 0;

            try {
                edad = Integer.parseInt(usuarioEdad);
                if (edad < 18) {
                    errores.addMessage("Edad debe ser mayor o igual a 18");
                }
            } catch (Exception e) {
                errores.addMessage("Edad");
            }

            if (telefonoTipos == null
                    || telefonoLadas == null
                    || telefonoNumeros == null
                    || telefonoTipos.length == 0
                    || telefonoTipos.length != telefonoLadas.length
                    || telefonoTipos.length != telefonoNumeros.length) {

                errores.addMessage("Telefonos");
            }

            List<Telefono> telefonos = new ArrayList<>();

            if (telefonoTipos != null
                    && telefonoLadas != null
                    && telefonoNumeros != null
                    && telefonoTipos.length == telefonoLadas.length
                    && telefonoTipos.length == telefonoNumeros.length) {

                for (int i = 0; i < telefonoTipos.length; i++) {

                    String tipo = telefonoTipos[i];
                    String ladaTexto = telefonoLadas[i];
                    String numero = telefonoNumeros[i];

                    if (tipo == null || tipo.isBlank()) {
                        errores.addMessage(
                                "Tipo de telefono " + (i + 1));
                        continue;
                    }

                    if (numero == null || numero.isBlank()) {
                        errores.addMessage(
                                "Numero de telefono " + (i + 1));
                        continue;
                    }

                    int lada;

                    try {
                        lada = Integer.parseInt(ladaTexto);
                    } catch (Exception e) {
                        errores.addMessage(
                                "Lada del telefono " + (i + 1));
                        continue;
                    }

                    telefonos.add(
                            Telefono.builder()
                                    .tipoTelefono(tipo.trim())
                                    .lada(lada)
                                    .numero(numero.trim())
                                    .build());
                }
            }

            if (!errores.getMessages().isEmpty()) {
                return new ResponseEntity<>(
                        errores,
                        HttpStatus.BAD_REQUEST);
            }

            Usuario.UsuarioBuilder usuarioBuilder = Usuario.builder();

            int id = 0;

            try {
                id = Integer.parseInt(usuarioId);
            } catch (Exception ignored) {
            }

            if (id > 0) {
                usuarioBuilder.idUsuario(id);
            }

            Usuario usuario = usuarioBuilder
                    .nombre(usuarioNombre.trim())
                    .edad(edad)
                    .telefonos(telefonos)
                    .build();

            ResponseWrapper<Usuario> user;

            if (id > 0) {
                user = feignUserService.updateUser(id, usuario);
            } else {
                user = feignUserService.createUser(usuario);
            }

            if (user == null || !user.isSuccess()) {
                ErrorResponse errorResponse = new ErrorResponse();

                if (user != null && user.getMessage() != null) {
                    errorResponse.addMessage(user.getMessage());
                } else {
                    errorResponse.addMessage(
                            "No fue posible guardar el usuario");
                }

                response = errorResponse;

                if (user != null
                        && user.getResponseEntity() != null) {

                    HttpStatus apiStatus = HttpStatus.resolve(
                            user.getResponseEntity().getStatusCodeValue());

                    if (apiStatus != null) {
                        statusCode = apiStatus;
                    } else {
                        statusCode = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    statusCode = HttpStatus.BAD_REQUEST;
                }
            }

        } catch (Exception e) {
            log.error("Error al guardar usuario", e);

            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.addMessage("Datos invalidos");

            response = errorResponse;
            statusCode = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(response, statusCode);
    }
}
