package com.proyecto.APIZum.service;

import com.proyecto.APIZum.DTO.ContactoDTO;
import com.proyecto.APIZum.error.excepciones.EntidadImprocesableException;
import com.proyecto.APIZum.error.excepciones.NoEncontradoException;
import com.proyecto.APIZum.model.Contacto;
import com.proyecto.APIZum.model.Usuario;
import com.proyecto.APIZum.repository.ContactoRepository;
import com.proyecto.APIZum.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactoService {

    private final ContactoRepository contactoRepository;
    private final UsuarioRepository usuarioRepository;

    public ContactoService(ContactoRepository contactoRepository, UsuarioRepository usuarioRepository) {
        this.contactoRepository = contactoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<ContactoDTO> listar(Authentication auth) {
        return contactoRepository.findByUsuarioUsername(auth.getName())
                .stream().map(this::toDTO).toList();
    }

    public List<ContactoDTO> listarFavoritos(Authentication auth) {
        return contactoRepository.findByUsuarioUsernameAndEsFavoritoTrue(auth.getName())
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public ContactoDTO agregar(Authentication auth, String contactoUsername, String alias) {
        if (auth.getName().equals(contactoUsername)) {
            throw new EntidadImprocesableException("No puedes agregarte a ti mismo como contacto");
        }
        if (contactoRepository.existsByUsuarioUsernameAndContactoUsername(auth.getName(), contactoUsername)) {
            throw new EntidadImprocesableException("Ese contacto ya existe en tu lista");
        }
        Usuario usuario = buscarUsuario(auth.getName());
        Usuario contacto = buscarUsuario(contactoUsername);
        return toDTO(contactoRepository.save(new Contacto(usuario, contacto, alias)));
    }

    @Transactional
    public ContactoDTO toggleFavorito(Authentication auth, Long id) {
        Contacto c = contactoRepository.findById(id)
                .filter(x -> x.getUsuario().getUsername().equals(auth.getName()))
                .orElseThrow(() -> new NoEncontradoException("Contacto no encontrado"));
        c.setEsFavorito(!c.isEsFavorito());
        return toDTO(c);
    }

    @Transactional
    public void eliminar(Authentication auth, Long id) {
        Contacto c = contactoRepository.findById(id)
                .filter(x -> x.getUsuario().getUsername().equals(auth.getName()))
                .orElseThrow(() -> new NoEncontradoException("Contacto no encontrado"));
        contactoRepository.delete(c);
    }

    private Usuario buscarUsuario(String username) {
        return usuarioRepository.findByUsernameAndActivoTrue(username)
                .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado: " + username));
    }

    private ContactoDTO toDTO(Contacto c) {
        Usuario u = c.getContacto();
        String numCuenta = (u.getCuenta() != null) ? u.getCuenta().getNumCuenta() : null;
        return new ContactoDTO(c.getId(), u.getUsername(), u.getNombre(), u.getApellidos(),
                u.getEmail(), numCuenta, c.getAlias(), c.isEsFavorito());
    }
}
