package com.bione.api.ecommerce.service;

import com.bione.api.ecommerce.dto.ClienteDTO;
import com.bione.api.ecommerce.exception.ClienteNotFoundException;
import com.bione.api.ecommerce.exception.ClienteInvalidException;
import com.bione.api.ecommerce.model.Cliente;
import com.bione.api.ecommerce.repository.ClienteRepository;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public ClienteService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Método para listar todos os clientes
    public List<ClienteDTO> listarClientes() {
        return clienteRepository.findAll()
                .stream()
                .map(ClienteDTO::new)
                .collect(Collectors.toList());
    }

    // ✅ Método para buscar cliente por ID sem erros
    public ClienteDTO buscarClientePorId(Long id) {
        return clienteRepository.findById(id)
                .map(ClienteDTO::new)  // ✅ Agora funciona
                .orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado no Banco de Dados", id));
    }

    // Método para salvar um novo cliente (incluindo criptografia de senha)
    // ✅ Método para salvar um novo cliente
    public ClienteDTO salvarCliente(ClienteDTO clienteDTO) {
        validarCampos(clienteDTO);
        verificarUnicidade(clienteDTO, null);

        // ✅ Criptografando a senha antes de salvar no banco
        if (clienteDTO.getSenha() != null) {
            clienteDTO.setSenha(passwordEncoder.encode(clienteDTO.getSenha()));
        }

        Cliente cliente = new Cliente(clienteDTO);
        clienteRepository.save(cliente);
        return new ClienteDTO(cliente);
    }

    @Transactional
    public ClienteDTO atualizarCliente(Long id, ClienteDTO clienteDTO) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado"));

        validarCampos(clienteDTO);
        verificarUnicidade(clienteDTO, id);

        cliente.setNome(clienteDTO.getNome());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setTelefone(clienteDTO.getTelefone());
        cliente.setEstado(clienteDTO.getEstado());
        cliente.setCidade(clienteDTO.getCidade());
        cliente.setBairro(clienteDTO.getBairro());
        cliente.setComplemento(clienteDTO.getComplemento());
        cliente.setRua(clienteDTO.getRua());
        cliente.setCep(clienteDTO.getCep());
        cliente.setNumero(clienteDTO.getNumero());

        if (clienteDTO.getSenha() != null && !clienteDTO.getSenha().isEmpty()) {
            cliente.setSenha(passwordEncoder.encode(clienteDTO.getSenha()));
        }

        clienteRepository.save(cliente);
        return new ClienteDTO(cliente);
    }

    // Método para deletar cliente
    public boolean deletarCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            return false;
        }
        clienteRepository.deleteById(id);
        return true;
    }

    // Verificação de unicidade de e-mail e telefone
    private void verificarUnicidade(ClienteDTO clienteDTO, Long clienteId) {
        Optional<Cliente> clienteExistente = clienteRepository.findByEmail(clienteDTO.getEmail());

        if (clienteExistente.isPresent() && !clienteExistente.get().getId().equals(clienteId)) {
            throw new ClienteInvalidException("E-mail já cadastrado.");
        }

        if (clienteRepository.existsByTelefone(clienteDTO.getTelefone()) &&
                (clienteId == null || !clienteExistente.get().getId().equals(clienteId))) {
            throw new ClienteInvalidException("Telefone já cadastrado.");
        }
    }

    // Validação dos campos
    private void validarCampos(ClienteDTO clienteDTO) {
        List<String> erros = new ArrayList<>();

        if (!validarEmail(clienteDTO.getEmail())) erros.add("E-mail inválido.");
        if (!validarSenha(clienteDTO.getSenha())) erros.add("Senha inválida.");
        if (!validarNome(clienteDTO.getNome())) erros.add("Nome inválido.");
        if (!validarTelefone(clienteDTO.getTelefone())) erros.add("Telefone inválido.");
        if (!validarCep(clienteDTO.getCep())) erros.add("CEP inválido.");

        if (!erros.isEmpty()) {
            throw new ClienteInvalidException(String.join(" ", erros));
        }
    }

    // Validações individuais
    private boolean validarEmail(String email) {
        return email != null && Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", email);
    }

    private boolean validarSenha(String senha) {
        return senha != null && Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=*!]).{6,8}$", senha);
    }

    private boolean validarNome(String nome) {
        return nome != null && Pattern.matches("^[A-Z][a-z]+(?:\\s[A-Z][a-z]+)+$", nome);
    }

    private boolean validarTelefone(String telefone) {
        return telefone != null && Pattern.matches("^\\d{10,11}$", telefone.replaceAll("\\D", ""));
    }

    private boolean validarCep(String cep) {
        return cep != null && Pattern.matches("^\\d{5}-?\\d{3}$", cep);
    }

    // Exportação de clientes para CSV
    public void exportarClientesParaCSV(List<ClienteDTO> clientes, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=clientes.csv");

        try (CSVWriter csvWriter = new CSVWriter(
                new OutputStreamWriter(response.getOutputStream(), "UTF-8"),
                ';', CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {

            String[] header = {"ID", "Nome", "Email", "Telefone", "Estado", "Cidade", "Bairro", "Complemento", "Rua", "CEP", "Número"};
            csvWriter.writeNext(header);

            for (ClienteDTO cliente : clientes) {
                csvWriter.writeNext(new String[]{
                        cliente.getId() != null ? cliente.getId().toString() : "",
                        cliente.getNome(),
                        cliente.getEmail(),
                        "=\"" + cliente.getTelefone() + "\"",
                        cliente.getEstado(),
                        cliente.getCidade(),
                        cliente.getBairro(),
                        cliente.getComplemento(),
                        cliente.getRua(),
                        cliente.getCep(),
                        cliente.getNumero()
                });
            }
        }
    }
}
