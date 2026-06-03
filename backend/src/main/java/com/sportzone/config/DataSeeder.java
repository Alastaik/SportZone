package com.sportzone.config;

import com.sportzone.model.Produto;
import com.sportzone.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// Insere produtos iniciais no catálogo se o banco estiver vazio
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ProdutoRepository produtoRepository;

    @Override
    public void run(String... args) {
        if (produtoRepository.count() > 0) {
            log.info("[SEED] Produtos já existem — seed ignorado");
            return;
        }

        log.info("[SEED] Inserindo produtos iniciais...");

        produtoRepository.save(Produto.builder()
                .nome("Tênis Runner Pro X1")
                .descricao("Tênis de corrida com amortecimento em gel e solado de carbono para máxima performance.")
                .preco(new BigDecimal("499.90"))
                .categoria("Calçados")
                .marca("SportZone")
                .quantidadeEstoque(50)
                .build());

        produtoRepository.save(Produto.builder()
                .nome("Camisa Oficial Brasil 2026")
                .descricao("Camisa oficial da seleção brasileira com tecnologia Dri-Fit e tecido respirável.")
                .preco(new BigDecimal("349.90"))
                .categoria("Camisas")
                .marca("SportZone")
                .quantidadeEstoque(100)
                .build());

        produtoRepository.save(Produto.builder()
                .nome("Bola Futebol Pro Match")
                .descricao("Bola de futebol profissional com costura térmica e aprovação FIFA Quality Pro.")
                .preco(new BigDecimal("199.90"))
                .categoria("Acessórios")
                .marca("SportZone")
                .quantidadeEstoque(80)
                .build());

        produtoRepository.save(Produto.builder()
                .nome("Short Training Elite")
                .descricao("Short de treino com tecnologia UV Protection e bolsos laterais com zíper.")
                .preco(new BigDecimal("129.90"))
                .categoria("Vestuário")
                .marca("SportZone")
                .quantidadeEstoque(120)
                .build());

        produtoRepository.save(Produto.builder()
                .nome("Mochila Sport 40L")
                .descricao("Mochila esportiva com compartimento para chuteiras, garrafa e notebook 15\".")
                .preco(new BigDecimal("249.90"))
                .categoria("Acessórios")
                .marca("SportZone")
                .quantidadeEstoque(60)
                .build());

        produtoRepository.save(Produto.builder()
                .nome("Meias Compressão Pro")
                .descricao("Meias de compressão graduada para melhor circulação e recuperação muscular.")
                .preco(new BigDecimal("59.90"))
                .categoria("Acessórios")
                .marca("SportZone")
                .quantidadeEstoque(200)
                .build());

        log.info("[SEED] 6 produtos inseridos com sucesso");
    }
}
