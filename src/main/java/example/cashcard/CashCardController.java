package example.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.List;
import java.net.URI;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.security.Principal;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {
  private CashCardRepository cashCardRepository;
  private CashCard cashCardUpdate;

  private CashCard findCashCard(Long requestedId, Principal principal) {
    return cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
  }

  public CashCardController(CashCardRepository cashCardRepository) {
    this.cashCardRepository = cashCardRepository;
  }

  @GetMapping("/{requestedId}")
  public ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {
    CashCard cashCard = findCashCard(requestedId, principal);
    if (cashCard != null) {
      return ResponseEntity.ok(cashCard);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping
  private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb) {
    CashCard savedCashCard = cashCardRepository.save(newCashCardRequest);
    URI locationOfNewCashCard = ucb
        .path("cashcards/{id}")
        .buildAndExpand(savedCashCard.id())
        .toUri();
    return ResponseEntity.created(locationOfNewCashCard).build();
  }

  @GetMapping
  public ResponseEntity<List<CashCard>> findAll(Pageable pageable) {
    Page<CashCard> page = cashCardRepository.findAll(
        PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))));
    return ResponseEntity.ok(page.getContent());
  }

  @PutMapping("/{requestedId}")
  private ResponseEntity<Void> putCashCard(@PathVariable Long requestedId, @RequestBody CashCard cashCardUpdate,
      Principal principal) {
    CashCard cashCard = findCashCard(requestedId, principal);
    if (cashCard != null) {
      CashCard updatedCashCard = new CashCard(cashCard.id(), cashCardUpdate.amount(), principal.getName());
      cashCardRepository.save(updatedCashCard);
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}")
  private ResponseEntity<Void> deleteCashCard(@PathVariable Long id) {
    return ResponseEntity.noContent().build();
  }
}
