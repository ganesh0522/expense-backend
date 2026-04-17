package com.xpensesplitter.repository;

import com.xpensesplitter.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}