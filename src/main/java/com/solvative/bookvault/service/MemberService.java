package com.solvative.bookvault.service;

import com.solvative.bookvault.api.dto.MemberCreateRequest;
import com.solvative.bookvault.api.dto.MemberResponse;
import com.solvative.bookvault.api.dto.MemberUpdateRequest;
import com.solvative.bookvault.domain.Member;
import com.solvative.bookvault.exception.ConflictException;
import com.solvative.bookvault.exception.ResourceNotFoundException;
import com.solvative.bookvault.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MemberService {

	private final MemberRepository memberRepository;
	private final Clock clock;

	public MemberService(MemberRepository memberRepository, Clock clock) {
		this.memberRepository = memberRepository;
		this.clock = clock;
	}

	@Transactional
	public MemberResponse createMember(MemberCreateRequest request) {
		if (memberRepository.existsByEmail(request.email())) {
			throw new ConflictException("EMAIL_ALREADY_EXISTS", "A member with this email already exists");
		}
		LocalDateTime joinedAt = LocalDateTime.now(clock);
		Member member = new Member(
				UUID.randomUUID(),
				request.email(),
				request.name(),
				request.membershipStatus(),
				joinedAt
		);
		return toResponse(memberRepository.save(member));
	}

	@Transactional
	public MemberResponse updateMember(UUID id, MemberUpdateRequest request) {
		Member member = memberRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("MEMBER_NOT_FOUND", "Member not found"));

		if (!member.getEmail().equals(request.email()) && memberRepository.existsByEmail(request.email())) {
			throw new ConflictException("EMAIL_ALREADY_EXISTS", "A member with this email already exists");
		}

		member.setEmail(request.email());
		member.setName(request.name());
		member.setMembershipStatus(request.membershipStatus());
		return toResponse(memberRepository.save(member));
	}

	@Transactional
	public void deleteMember(UUID id) {
		Member member = memberRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("MEMBER_NOT_FOUND", "Member not found"));
		memberRepository.delete(member);
	}

	public MemberResponse getMember(UUID id) {
		return memberRepository.findById(id)
				.map(this::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("MEMBER_NOT_FOUND", "Member not found"));
	}

	public List<MemberResponse> searchMembers(String q) {
		String query = q == null ? "" : q.trim();
		if (query.isBlank()) {
			return List.of();
		}
		return memberRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query).stream()
				.map(this::toResponse)
				.toList();
	}

	private MemberResponse toResponse(Member member) {
		return new MemberResponse(
				member.getId(),
				member.getEmail(),
				member.getName(),
				member.getMembershipStatus(),
				member.getJoinedAt()
		);
	}
}

