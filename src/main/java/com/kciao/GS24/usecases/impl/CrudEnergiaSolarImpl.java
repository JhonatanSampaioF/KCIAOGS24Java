package com.kciao.GS24.usecases.impl;

import com.kciao.GS24.domains.Endereco;
import com.kciao.GS24.domains.EnergiaSolar;
import com.kciao.GS24.gateways.controllers.interfaces.EnergiaEolicaController;
import com.kciao.GS24.gateways.controllers.interfaces.EnergiaSolarController;
import com.kciao.GS24.gateways.repositories.EnderecoRepository;
import com.kciao.GS24.gateways.repositories.EnergiaSolarRepository;
import com.kciao.GS24.gateways.requests.energiaSolar.EnergiaSolarRequestPatchDto;
import com.kciao.GS24.gateways.requests.energiaSolar.EnergiaSolarRequestPostDto;
import com.kciao.GS24.gateways.responses.EnergiaSolarResponseDto;
import com.kciao.GS24.usecases.interfaces.CrudEnergiaSolar;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
public class CrudEnergiaSolarImpl implements CrudEnergiaSolar {

    private final EnergiaSolarRepository energiaSolarRepository;
    private final EnderecoRepository enderecoRepository;

    @Override
    public EnergiaSolarResponseDto save(EnergiaSolarRequestPostDto energiaSolar) {

        Optional<Endereco> enderecoOptional = enderecoRepository.findById(energiaSolar.getFk_endereco());
        Endereco endereco = enderecoOptional.orElseThrow(() -> new RuntimeException("Endereco não encontrado com o ID: " + energiaSolar.getFk_endereco()));
        
        EnergiaSolar energiaSolarASerCriada = EnergiaSolar.builder()
                .areaPlaca(energiaSolar.getAreaPlaca())
                .irradiacaoSolar(energiaSolar.getIrradiacaoSolar())
                .energiaEstimadaGerada(energiaSolar.getEnergiaEstimadaGerada())
                .fk_endereco(endereco)
                .build();
        
        EnergiaSolar energiaSolarSalva = energiaSolarRepository.save(energiaSolarASerCriada);
        
        EnergiaSolarResponseDto energiaSolarResponse = EnergiaSolarResponseDto.builder()
                .id(energiaSolarSalva.getId())
                .areaPlaca(energiaSolarSalva.getAreaPlaca())
                .irradiacaoSolar(energiaSolarSalva.getIrradiacaoSolar())
                .energiaEstimadaGerada(energiaSolarSalva.getEnergiaEstimadaGerada())
                .fk_endereco(energiaSolarSalva.getFk_endereco().getId())
                .build();

        energiaSolarResponse.add(
                linkTo(
                        methodOn(EnergiaSolarController.class)
                                .buscarEnergiaSolar(energiaSolarSalva.getId())
                ).withSelfRel()
        );
        
        return energiaSolarResponse;
        
    }

    @Override
    public Optional<EnergiaSolarResponseDto> findById(Integer id) {
        
        Optional<EnergiaSolar> energiaSolar = energiaSolarRepository.findById(id);
        
        if (energiaSolar.isPresent()) {
            EnergiaSolarResponseDto energiaSolarResponse = EnergiaSolarResponseDto.builder()
                    .id(energiaSolar.get().getId())
                    .areaPlaca(energiaSolar.get().getAreaPlaca())
                    .irradiacaoSolar(energiaSolar.get().getIrradiacaoSolar())
                    .energiaEstimadaGerada(energiaSolar.get().getEnergiaEstimadaGerada())
                    .fk_endereco(energiaSolar.get().getFk_endereco().getId())
                    .build();
            return Optional.of(energiaSolarResponse);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Page<EnergiaSolarResponseDto> findAll(Pageable pageable) {

        Page<EnergiaSolar> pageEnergiaSolar = energiaSolarRepository.findAll(pageable);

        Page<EnergiaSolarResponseDto> pageEnergiaSolarResponse = pageEnergiaSolar
                .map(energiaSolar -> EnergiaSolarResponseDto.builder()
                        .id(energiaSolar.getId())
                        .areaPlaca(energiaSolar.getAreaPlaca())
                        .irradiacaoSolar(energiaSolar.getIrradiacaoSolar())
                        .energiaEstimadaGerada(energiaSolar.getEnergiaEstimadaGerada())
                        .fk_endereco(energiaSolar.getFk_endereco().getId())
                        .build());

        return pageEnergiaSolarResponse;
    }

    @Override
    public Optional<EnergiaSolarResponseDto> update(Integer id, EnergiaSolarRequestPatchDto energiaSolar) {

        EnergiaSolar energiaSolarASerAtualizada = EnergiaSolar.builder()
                .areaPlaca(energiaSolar.getAreaPlaca())
                .irradiacaoSolar(energiaSolar.getIrradiacaoSolar())
                .energiaEstimadaGerada(energiaSolar.getEnergiaEstimadaGerada())
                .build();

        int energiaSolarAtualizada = energiaSolarRepository.updateById(
                id,
                energiaSolarASerAtualizada.getAreaPlaca(),
                energiaSolarASerAtualizada.getIrradiacaoSolar(),
                energiaSolarASerAtualizada.getEnergiaEstimadaGerada()
        );

        if (energiaSolarAtualizada != 0) {
            Optional<EnergiaSolarResponseDto> energiaSolarResponse = findById(id);
            if (energiaSolarResponse.isPresent()) {
                return energiaSolarResponse;
            }
            return Optional.empty();
        } else {
            return Optional.empty();
        }

    }

    @Override
    public void delete(Integer id) {
        energiaSolarRepository.deleteById(id);
    }
}
