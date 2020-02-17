package com.ankr.topFinances.model.repository;

import java.util.Optional;

import com.ankr.topFinances.model.entity.Usuario;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void deveRetornarVerdadeiroQuandoHouverAExistenciaDeUmEmail() {
        //cenário
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);

        //ação/ execução
        boolean result = repository.existsByEmail("teste@email.com");

        //verificação
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void deveRetornarFalsoQuandoNaoHouverAExistenciaDeUmEmail() {

        //ação/ execução
        boolean result = repository.existsByEmail("teste@email.com");

        //verificação
        Assertions.assertThat(result).isFalse();

    }

    @Test
    public void devePersistirUmUsuarioNaBaseDeDados() {
        //cenario
        Usuario usuario = criarUsuario();

        //ação   
        Usuario usuarioSalvo = repository.save(usuario);

        //verificação
        Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
    }

    @Test
    public void deveBuscarUmUsuarioPorEmail() {
        //cenario
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);

        //verificação
        Optional<Usuario> result = repository.findByEmail("teste@email.com");

        Assertions.assertThat(result.isPresent()).isTrue();

    }

    @Test
    public void deveRestornarVazioAoBuscarUmUsuarioPorEmailQuandoNaoExisteNaBase() {
        //verificação
        Optional<Usuario> result = repository.findByEmail("teste@email.com");

        Assertions.assertThat(result.isPresent()).isFalse();


    }

    public static Usuario criarUsuario() {
        Usuario usuario = Usuario
            .builder()
            .nome("usuario")
            .email("teste@email.com")
            .senha("senha")
            .build();

        return usuario;
    }

}