package school.sptech.wmw_projeto_pi.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import school.sptech.wmw_projeto_pi.dto.interno.MovimentacaoPeriodoDto;
import school.sptech.wmw_projeto_pi.entity.Insumo;
import school.sptech.wmw_projeto_pi.entity.MovimentacaoEstoque;
import school.sptech.wmw_projeto_pi.entity.Usuario;
import school.sptech.wmw_projeto_pi.enums.MotivoMovimentacaoEnum;
import school.sptech.wmw_projeto_pi.enums.TipoMovimentacaoEnum;
import school.sptech.wmw_projeto_pi.enums.UnidadeMedidaEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:dashboard;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
class MovimentacaoEstoqueRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    @Test
    void deveProjetarMovimentacoesDoPeriodo() {
        Usuario usuario = new Usuario();
        usuario.setNome("Teste");
        usuario.setEmail("teste@example.com");
        usuario.setSenha("senha");
        usuario.setAtivo(true);
        entityManager.persist(usuario);

        Insumo insumo = new Insumo();
        insumo.setNome("Mussarela");
        insumo.setUnidadeMedida(UnidadeMedidaEnum.GRAMA);
        insumo.setQuantidadeAtual(new BigDecimal("1000.000"));
        insumo.setPercentualPerda(0);
        insumo.setAtivo(true);
        insumo.setCreatedBy(usuario);
        entityManager.persist(insumo);

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setInsumo(insumo);
        movimentacao.setUsuario(usuario);
        movimentacao.setTipo(TipoMovimentacaoEnum.SAIDA);
        movimentacao.setMotivo(MotivoMovimentacaoEnum.QUEBRA);
        movimentacao.setQuantidade(new BigDecimal("12.500"));
        movimentacao.setObservacao("teste");
        entityManager.persist(movimentacao);
        entityManager.flush();

        LocalDateTime inicio = LocalDate.now().minusDays(29).atStartOfDay();
        LocalDateTime fim = LocalDate.now().plusDays(1).atStartOfDay();

        List<MovimentacaoPeriodoDto> resultado = movimentacaoEstoqueRepository.buscarNoPeriodo(inicio, fim);

        assertThat(resultado).hasSize(1);
        MovimentacaoPeriodoDto dto = resultado.get(0);
        assertThat(dto.insumoId()).isEqualTo(insumo.getId());
        assertThat(dto.nome()).isEqualTo("Mussarela");
        assertThat(dto.unidadeMedida()).isEqualTo(UnidadeMedidaEnum.GRAMA);
        assertThat(dto.ativo()).isTrue();
        assertThat(dto.tipo()).isEqualTo(TipoMovimentacaoEnum.SAIDA);
        assertThat(dto.motivo()).isEqualTo(MotivoMovimentacaoEnum.QUEBRA);
        assertThat(dto.quantidade()).isEqualByComparingTo("12.500");
        assertThat(dto.dataHora()).isNotNull();

        assertThat(movimentacaoEstoqueRepository.buscarNoPeriodo(fim, fim.plusDays(1))).isEmpty();
    }
}
