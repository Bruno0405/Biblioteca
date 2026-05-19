package biblioteca.reflection;

import io.quarkus.runtime.annotations.RegisterForReflection;

import biblioteca.autores.data.Autor;
import biblioteca.autores.models.AutorDTO;
import biblioteca.auth.models.LoginRequest;
import biblioteca.auth.models.LoginResponse;
import biblioteca.clientes.data.Cliente;
import biblioteca.clientes.models.ClienteDTO;
import biblioteca.estoque.data.Estoque;
import biblioteca.estoque.models.EstoqueDTO;
import biblioteca.fotos.data.Foto;
import biblioteca.fotos.models.FotoDTO;
import biblioteca.funcionarios.data.Funcionario;
import biblioteca.funcionarios.models.FuncionarioDTO;
import biblioteca.generos.data.Genero;
import biblioteca.generos.models.GeneroDTO;
import biblioteca.historico.data.HistoricoCliente;
import biblioteca.historico.models.HistoricoClienteDTO;
import biblioteca.livros.data.Livro;
import biblioteca.livros.models.LivroDTO;
import biblioteca.logs.data.Log;
import biblioteca.logs.models.LogDTO;
import biblioteca.movimentacao.data.MovimentacaoEstoque;
import biblioteca.movimentacao.models.MovimentacaoEstoqueDTO;
import biblioteca.multas.data.Multa;
import biblioteca.multas.models.MultaDTO;
import biblioteca.reservas.data.Reserva;
import biblioteca.reservas.models.ReservaDTO;

@RegisterForReflection(targets = {
        Autor.class, AutorDTO.class,
        Cliente.class, ClienteDTO.class,
        Estoque.class, EstoqueDTO.class,
        Foto.class, FotoDTO.class,
        Funcionario.class, FuncionarioDTO.class,
        Genero.class, GeneroDTO.class,
        HistoricoCliente.class, HistoricoClienteDTO.class,
        Livro.class, LivroDTO.class,
        Log.class, LogDTO.class,
        MovimentacaoEstoque.class, MovimentacaoEstoqueDTO.class,
        Multa.class, MultaDTO.class,
        Reserva.class, ReservaDTO.class,
        LoginRequest.class,
        LoginResponse.class
})
public class ReflectionConfig {
}
