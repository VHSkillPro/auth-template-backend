package com.vhskillpro.backend.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.vhskillpro.backend.modules.user.User;
import com.vhskillpro.backend.modules.user.UserSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserSpecification Tests")
class UserSpecificationTests {

  @Mock private Root<User> root;
  @Mock private CriteriaBuilder cb;
  @Mock private Predicate predicate;

  @SuppressWarnings("unchecked")
  @Test
  @DisplayName("keywordContainingIgnoreCase returns conjunction when keyword blank")
  void keywordContainingIgnoreCase_returnsConjunction_whenBlank() {
    when(cb.conjunction()).thenReturn(predicate);
    Predicate result =
        UserSpecification.keywordContainingIgnoreCase(" ").toPredicate(root, null, cb);
    assertThat(result).isSameAs(predicate);
  }

  @SuppressWarnings("unchecked")
  @Test
  @DisplayName("keywordContainingIgnoreCase builds OR on email and full name when keyword present")
  void keywordContainingIgnoreCase_buildsOr_whenKeywordPresent() {
    Path<String> email = (Path<String>) (Path<?>) org.mockito.Mockito.mock(Path.class);
    Path<String> first = (Path<String>) (Path<?>) org.mockito.Mockito.mock(Path.class);
    Path<String> last = (Path<String>) (Path<?>) org.mockito.Mockito.mock(Path.class);
    Expression<String> fullName = (Expression<String>) org.mockito.Mockito.mock(Expression.class);
    Expression<String> lowerEmail = (Expression<String>) org.mockito.Mockito.mock(Expression.class);
    Predicate likeEmail = org.mockito.Mockito.mock(Predicate.class);
    Predicate likeFull = org.mockito.Mockito.mock(Predicate.class);
    Expression<String> lowerFull = (Expression<String>) org.mockito.Mockito.mock(Expression.class);

    when(root.<String>get("email")).thenReturn(email);
    when(root.<String>get("firstName")).thenReturn(first);
    when(root.<String>get("lastName")).thenReturn(last);
    when(cb.concat(first, " ")).thenReturn(fullName);
    when(cb.concat(fullName, last)).thenReturn(fullName);
    when(cb.lower(email)).thenReturn(lowerEmail);
    when(cb.lower(fullName)).thenReturn(lowerFull);
    when(cb.like(lowerEmail, "%john%")).thenReturn(likeEmail);
    when(cb.like(lowerFull, "%john%")).thenReturn(likeFull);
    when(cb.or(likeEmail, likeFull)).thenReturn(predicate);

    Predicate result =
        UserSpecification.keywordContainingIgnoreCase("John").toPredicate(root, null, cb);
    assertThat(result).isSameAs(predicate);
  }

  @SuppressWarnings("unchecked")
  @Test
  @DisplayName("hasEnabledStatus returns conjunction when blank; true->isTrue, false->isFalse")
  void hasEnabledStatus_behaviors() {
    Path<Boolean> enabledPath = (Path<Boolean>) (Path<?>) org.mockito.Mockito.mock(Path.class);
    Predicate conj = org.mockito.Mockito.mock(Predicate.class);
    Predicate isTrue = org.mockito.Mockito.mock(Predicate.class);
    Predicate isFalse = org.mockito.Mockito.mock(Predicate.class);
    when(cb.conjunction()).thenReturn(conj);
    when(root.<Boolean>get("enabled")).thenReturn(enabledPath);
    when(cb.isTrue(enabledPath)).thenReturn(isTrue);
    when(cb.isFalse(enabledPath)).thenReturn(isFalse);

    assertThat(UserSpecification.hasEnabledStatus(null).toPredicate(root, null, cb)).isSameAs(conj);
    assertThat(UserSpecification.hasEnabledStatus("").toPredicate(root, null, cb)).isSameAs(conj);
    assertThat(UserSpecification.hasEnabledStatus("true").toPredicate(root, null, cb))
        .isSameAs(isTrue);
    assertThat(UserSpecification.hasEnabledStatus("false").toPredicate(root, null, cb))
        .isSameAs(isFalse);
  }

  @SuppressWarnings("unchecked")
  @Test
  @DisplayName("hasLockedStatus returns conjunction when blank; true/false branches")
  void hasLockedStatus_behaviors() {
    Path<Boolean> lockedPath = (Path<Boolean>) (Path<?>) org.mockito.Mockito.mock(Path.class);
    Predicate conj = org.mockito.Mockito.mock(Predicate.class);
    Predicate isTrue = org.mockito.Mockito.mock(Predicate.class);
    Predicate isFalse = org.mockito.Mockito.mock(Predicate.class);
    when(cb.conjunction()).thenReturn(conj);
    when(root.<Boolean>get("locked")).thenReturn(lockedPath);
    when(cb.isTrue(lockedPath)).thenReturn(isTrue);
    when(cb.isFalse(lockedPath)).thenReturn(isFalse);

    assertThat(UserSpecification.hasLockedStatus(null).toPredicate(root, null, cb)).isSameAs(conj);
    assertThat(UserSpecification.hasLockedStatus("true").toPredicate(root, null, cb))
        .isSameAs(isTrue);
    assertThat(UserSpecification.hasLockedStatus("false").toPredicate(root, null, cb))
        .isSameAs(isFalse);
  }

  @SuppressWarnings("unchecked")
  @Test
  @DisplayName("hasSuperuserStatus returns conjunction when blank; true/false branches")
  void hasSuperuserStatus_behaviors() {
    Path<Boolean> suPath = (Path<Boolean>) (Path<?>) org.mockito.Mockito.mock(Path.class);
    Predicate conj = org.mockito.Mockito.mock(Predicate.class);
    Predicate isTrue = org.mockito.Mockito.mock(Predicate.class);
    Predicate isFalse = org.mockito.Mockito.mock(Predicate.class);
    when(cb.conjunction()).thenReturn(conj);
    when(root.<Boolean>get("superuser")).thenReturn(suPath);
    when(cb.isTrue(suPath)).thenReturn(isTrue);
    when(cb.isFalse(suPath)).thenReturn(isFalse);

    assertThat(UserSpecification.hasSuperuserStatus(null).toPredicate(root, null, cb))
        .isSameAs(conj);
    assertThat(UserSpecification.hasSuperuserStatus("true").toPredicate(root, null, cb))
        .isSameAs(isTrue);
    assertThat(UserSpecification.hasSuperuserStatus("false").toPredicate(root, null, cb))
        .isSameAs(isFalse);
  }

  @SuppressWarnings("unchecked")
  @Test
  @DisplayName("roleNameContainingIgnoreCase returns conjunction when blank; like when provided")
  void roleNameContainingIgnoreCase_behaviors() {
    Join<Object, Object> join = (Join<Object, Object>) org.mockito.Mockito.mock(Join.class);
    Path<String> roleNamePath = (Path<String>) (Path<?>) org.mockito.Mockito.mock(Path.class);
    Expression<String> lower = (Expression<String>) org.mockito.Mockito.mock(Expression.class);
    Predicate like = org.mockito.Mockito.mock(Predicate.class);
    Predicate conj = org.mockito.Mockito.mock(Predicate.class);

    when(cb.conjunction()).thenReturn(conj);
    when(root.join("role")).thenReturn(join);
    when(join.get("name")).thenReturn((Path) roleNamePath);
    when(cb.lower(roleNamePath)).thenReturn(lower);
    when(cb.like(lower, "%admin%")).thenReturn(like);

    assertThat(UserSpecification.roleNameContainingIgnoreCase(null).toPredicate(root, null, cb))
        .isSameAs(conj);
    assertThat(UserSpecification.roleNameContainingIgnoreCase("").toPredicate(root, null, cb))
        .isSameAs(conj);
    assertThat(UserSpecification.roleNameContainingIgnoreCase("ADMIN").toPredicate(root, null, cb))
        .isSameAs(like);
  }
}
