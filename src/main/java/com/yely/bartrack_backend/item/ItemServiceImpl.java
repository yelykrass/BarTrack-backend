package com.yely.bartrack_backend.item;

import org.springframework.security.access.AccessDeniedException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.yely.bartrack_backend.domain.ResourceNotFoundException;
import com.yely.bartrack_backend.implementations.IGenericService;
import com.yely.bartrack_backend.user.UserEntity;
import com.yely.bartrack_backend.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements IGenericService<ItemDTOResponse, ItemDTORequest> {

    private final ItemRepository repository;
    private final UserService userService;

    @Override
    public List<ItemDTOResponse> getEntities() {
        return repository.findAll()
                .stream()
                .map(ItemMapper::toDTO)
                .toList();
    }

    @Override
    public ItemDTOResponse storeEntity(ItemDTORequest dto) {
        UserEntity currentUser = userService.getCurrentUser();
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new AccessDeniedException("Only admin can create items");
        }

        // Створюємо товар від імені адміна
        ItemEntity entity = ItemMapper.toEntity(dto, currentUser);
        return ItemMapper.toDTO(repository.save(entity));
    }

    @Override
    public ItemDTOResponse showById(Long id) {
        return repository.findById(id)
                .map(ItemMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
    }

    @Override
    public ItemDTOResponse update(Long id, ItemDTORequest dto) {
        ItemEntity existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        UserEntity currentUser = userService.getCurrentUser(); // метод для BasicAuth
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ROLE_ADMIN"));

        if (isAdmin) {
            existing.setName(dto.name());
            existing.setCategory(dto.category());
            existing.setPrice(dto.price());
            existing.setQuantity(dto.quantity());
            existing.setExpiryDate(dto.expiryDate());
        } else {
            // бармен може змінювати лише quantity
            existing.setQuantity(dto.quantity());
        }

        return ItemMapper.toDTO(repository.save(existing));
    }

    @Override
    public void delete(Long id) {
        ItemEntity existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        UserEntity currentUser = userService.getCurrentUser();
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new AccessDeniedException("Only admin can delete items");
        }

        repository.delete(existing);
    }
}