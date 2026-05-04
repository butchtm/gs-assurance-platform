package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.Invoice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends CrudRepository<Invoice, Integer> {}
