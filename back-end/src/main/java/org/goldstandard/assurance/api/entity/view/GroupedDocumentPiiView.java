package org.goldstandard.assurance.api.entity.view;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.enumeration.PiiProbability;

@Entity(name = "v_grouped_document_pii")
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Builder(toBuilder = true)
@Getter
public class GroupedDocumentPiiView {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private final Integer id;

  private final UUID documentId;
  private final String entity;

  @Enumerated(EnumType.STRING)
  private final PiiProbability probability;

  private final Integer pageNumber;
  private final String location;
  private final Integer count;
}
