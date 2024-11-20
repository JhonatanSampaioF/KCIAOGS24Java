package com.kciao.GS24.usecases.impl;

import com.kciao.GS24.domains.Usuario;
import com.kciao.GS24.gateways.repositories.UsuarioRepository;
import com.kciao.GS24.gateways.requests.UsuarioRequestDto;
import com.kciao.GS24.gateways.responses.UsuarioResponseDto;
import com.kciao.GS24.usecases.interfaces.CrudUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudUsuarioImpl implements CrudUsuario {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UsuarioResponseDto save(UsuarioRequestDto usuario) {

        Usuario usuarioASerCriado = Usuario.builder()
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .build();

        Usuario usuarioSalvo = usuarioRepository.save(usuarioASerCriado);

        UsuarioResponseDto usuarioResponse = UsuarioResponseDto.builder()
                .nome(usuarioSalvo.getNome())
                .email(usuarioSalvo.getEmail())
                .build();

        return usuarioResponse;
    }

    @Override
    public Optional<UsuarioResponseDto> findById(Integer id) {

        Optional<Usuario> usuario = usuarioRepository.findById(id);

        if (usuario.isPresent()) {
            UsuarioResponseDto usuarioResponse = UsuarioResponseDto.builder()
                    .nome(usuario.get().getNome())
                    .email(usuario.get().getEmail())
                    .build();
            return Optional.of(usuarioResponse);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<UsuarioResponseDto> findAll() {

        List<Usuario> listUsuario = usuarioRepository.findAll();

        List<UsuarioResponseDto> listUsuarioResponse = listUsuario.stream()
                .map(usuario -> UsuarioResponseDto.builder()
                        .nome(usuario.getNome())
                        .email(usuario.getEmail())
                        .build()).toList();

        return listUsuarioResponse;
    }

    @Override
    public Optional<UsuarioResponseDto> update(Integer id, UsuarioRequestDto usuario) {

        Usuario usuarioASerAtualizado = Usuario.builder()
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .build();

        Optional<Usuario> usuarioAtualizado = usuarioRepository.updateById(
                id,
                usuarioASerAtualizado
        );

        if (usuarioAtualizado.isPresent()) {
            Optional<UsuarioResponseDto> usuarioResponse = findById(id);
            if (usuarioResponse.isPresent()) {
                return usuarioResponse;
            }
            return Optional.empty();
        } else {
            return Optional.empty();
        }

    }

    @Override
    public void delete(Integer id) {
        usuarioRepository.deleteById(id);
    }
}