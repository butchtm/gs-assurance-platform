package org.goldstandard.assurance.api.entity;

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
import lombok.With;
import org.goldstandard.assurance.api.enumeration.PiiProbability;

@Entity
@Getter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class DocumentProbablePii {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private final Integer id;

  private final UUID documentId;
  private final String entity;

  @Enumerated(EnumType.STRING)
  private final PiiProbability probability;

  @With private final Integer pageNumber;

  private final String location;
}
