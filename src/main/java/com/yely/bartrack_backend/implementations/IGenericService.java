package com.yely.bartrack_backend.implementations;

import java.util.List;

public interface IGenericService<T, S> {

    public List<T> getEntities();

    public T storeEntity(S dto);

    public T showById(Long id);

    T update(Long id, S dto);

    void delete(Long id);
}
