package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.TAC_ROLE;

import jakarta.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.converter.CommentConverter;
import org.goldstandard.assurance.api.dto.CommentCreateDTO;
import org.goldstandard.assurance.api.dto.CommentDTO;
import org.goldstandard.assurance.api.dto.CommentDelete;
import org.goldstandard.assurance.api.dto.CommentUpdateDTO;
import org.goldstandard.assurance.api.service.CommentService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CommentController {

  private final CommentService commentService;
  private final CommentConverter commentConverter;

  @RolesAllowed(value = {TAC_ROLE})
  @PostMapping("/comments")
  public ResponseEntity<CommentDTO> saveNewComment(@RequestBody CommentCreateDTO newComment) {
    return ResponseEntity.ok(commentConverter.toDto(commentService.saveNewComment(newComment)));
  }

  @RolesAllowed(value = {ARM_TEAM_ROLE, ADMIN_ROLE})
  @Caching(
      evict = {
        @CacheEvict(value = "publicGetAllStakeholderCache", allEntries = true),
        @CacheEvict(value = "publicGetOneStakeholderCache", allEntries = true)
      })
  @PutMapping("/comments")
  public ResponseEntity<Optional<CommentDTO>> updateComment(
      @RequestBody CommentUpdateDTO commentUpdate) {
    return ResponseEntity.ok(commentConverter.toDto(commentService.updateComment(commentUpdate)));
  }

  @GetMapping("/comments/{requestId}")
  public ResponseEntity<List<CommentDTO>> getAllCommentsByRequestId(
      @PathVariable Integer requestId) {
    return ResponseEntity.ok(
        commentService.getCommentsByRequestId(requestId).stream()
            .map(commentConverter::toDto)
            .toList());
  }

  @RolesAllowed(value = {ARM_TEAM_ROLE, ADMIN_ROLE})
  @DeleteMapping("/comments")
  public ResponseEntity<Void> deleteComment(@RequestBody CommentDelete commentDelete) {
    commentService.deleteComment(commentDelete);
    return ResponseEntity.noContent().build();
  }
}
