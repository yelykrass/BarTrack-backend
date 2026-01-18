package com.yely.bartrack_backend.item;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yely.bartrack_backend.domain.ConflictException;
import com.yely.bartrack_backend.domain.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository repository;

    public List<ItemDTOResponse> getEntities(boolean onlyActive) {
        List<ItemEntity> items = onlyActive
                ? repository.findByActiveTrue()
                : repository.findAll();

        return items.stream()
                .map(ItemMapper::toDTO)
                .toList();
    }

    @Transactional
    public ItemDTOResponse storeEntity(ItemDTORequest dto) {
        if (repository.existsByName(dto.name())) {
            throw new ConflictException("Item with name '" + dto.name() + "' already exists");
        }

        ItemEntity entity = ItemMapper.toEntity(dto);
        return ItemMapper.toDTO(repository.save(entity));
    }

    public ItemDTOResponse showById(Long id) {
        return repository.findById(id)
                .map(ItemMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }

    @Transactional
    public ItemDTOResponse update(Long id, ItemDTORequest dto) {
        ItemEntity existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        if (!existing.getName().equals(dto.name()) && repository.existsByName(dto.name())) {
            throw new ConflictException("Cannot rename: item with name '" + dto.name() + "' already exists");
        }
        existing.setName(dto.name());
        existing.setCategory(dto.category());
        existing.setPrice(dto.price());
        existing.setQuantity(dto.quantity());

        if (dto.active() != null) {
            existing.setActive(dto.active());
        }

        return ItemMapper.toDTO(repository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Item not found");
        }
        repository.deleteById(id);
    }

    public ItemEntity getEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }
}
