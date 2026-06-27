package br.com.fiap.oficina.autenticacao.adapter.in.rest;

import br.com.fiap.oficina.autenticacao.adapter.in.rest.request.AutenticarUsuarioRequest;
import br.com.fiap.oficina.autenticacao.adapter.in.rest.response.AutenticacaoResponse;
import br.com.fiap.oficina.autenticacao.application.command.AutenticarUsuarioCommand;
import br.com.fiap.oficina.autenticacao.application.port.in.AutenticarUsuarioUseCase;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    public AuthController(AutenticarUsuarioUseCase autenticarUsuarioUseCase) {
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
    }

    @PostMapping("/login")
    public AutenticacaoResponse autenticar(@Valid @RequestBody AutenticarUsuarioRequest request) {
        var token = autenticarUsuarioUseCase.autenticar(new AutenticarUsuarioCommand(request.login(), request.senha()));
        return new AutenticacaoResponse(token);
    }
}