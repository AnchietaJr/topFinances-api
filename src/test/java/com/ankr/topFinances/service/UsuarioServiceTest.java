package com.ankr.topFinances.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import com.ankr.topFinances.exception.ErroAutenticacao;
import com.ankr.topFinances.exception.RegraNegocioException;
import com.ankr.topFinances.model.entity.Usuario;
import com.ankr.topFinances.model.repository.UsuarioRepository;
import com.ankr.topFinances.service.impl.UsuarioServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @SpyBean
    UsuarioServiceImpl service;

    @MockBean
    UsuarioRepository repository;


    @Test
    public void deveSalvarUsuario() {
        //cenário
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario.builder()
            .id(1l)
            .nome("nome")
            .email("teste@email.com")
            .senha("senha")
            .build();

        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        //ação e verificao (Exception)
        assertDoesNotThrow(() -> {
            service.salvarUsuario(new Usuario());
        });

        //ação
        Usuario usuarioSalvo = service.salvarUsuario(new Usuario());

        //verificação (campos)
        assertTrue(!usuarioSalvo.equals(null));
        assertTrue(usuarioSalvo.getId() == 1l);
        assertTrue(usuarioSalvo.getNome() == "nome");
        assertTrue(usuarioSalvo.getEmail() == "teste@email.com");
        assertTrue(usuarioSalvo.getSenha() == "senha");  
    }

    @Test
    public void naoDeveSalvarUsuarioComEmailJaCadastrado() {
        //cenario
        String email = "teste@email.com";
        Usuario usuario = Usuario.builder().email(email).build();
        Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);

        //ação e verificação (exception)
        assertThrows(RegraNegocioException.class, () -> {
            //ação
            service.salvarUsuario(usuario);
        });

        Mockito.verify(repository, Mockito.never()).save(usuario);
    }

    @Test
    public void deveAutenticarUmUsuarioComSucesso() {
        //cenário
        String email = "teste@email.com";
        String senha = "";

        Usuario usuario = Usuario.builder()
            .email(email)
            .senha(senha)
            .id(1l)
            .build();
        
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

        //ação e verificação
        assertDoesNotThrow(() -> {
            service.autenticar(email, senha);
        });
    }

    @Test
    public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
        //cenário
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ErroAutenticacao.class, () -> {
            //ação
            service.autenticar("teste@email.com", "senha");
        });

        //verificação
        String expectedMessage = "Usuario não encontrado para o email informado.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void deveLancarErroQuandoASenhaInformadaNaoBater() {
        //cenário
        String senha = "senha";
        String email = "teste@email.com";
        Usuario usuario = Usuario.builder().email(email).senha(senha).build();
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

        Exception exception = assertThrows(ErroAutenticacao.class, () -> {
            //ação
            service.autenticar(email, "123");
        });

        //verificação
        String expectedMessage = "Senha inválida.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void deveValidarEmail() {
        //cenario
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

        //acao
        service.validarEmail("teste@email.com");
    }

    @Test
    public void deveLancarErroQuandoExistirEmailCadastrado() {
        //cenario
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

        Exception exception = assertThrows(RegraNegocioException.class, () -> {
            //acao
            service.validarEmail("teste@email.com");
        });

        //verificação
        String expectedMessage = "Já existe um usuário cadastrado com este email.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

}