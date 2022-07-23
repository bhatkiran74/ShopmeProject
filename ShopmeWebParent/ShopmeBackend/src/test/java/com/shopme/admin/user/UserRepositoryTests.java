package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

	@Autowired
	private UserRepository repo;

	@Autowired
	private TestEntityManager entityManager;

	// to create one user with one role
	@Test
	public void testCreateNewUserWithOneRole() {
		Role roleAdmin = entityManager.find(Role.class, 1);

		User userKiran = new User("bhatkiran74@gmail.com", "123456789", "kiransing", "bhat");
		userKiran.addRole(roleAdmin);

		User savedUser = repo.save(userKiran);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}

	// to create one user with Two role
	@Test
	public void testCreateNewUserWithTwoRole() {

		User userManish = new User("manish@gmail.com", "12345679", "manish", "gaikwad");

		Role roleEditor = new Role(3);
		Role roleAssistant = new Role(5);

		userManish.addRole(roleEditor);
		userManish.addRole(roleAssistant);

		User savedUser = repo.save(userManish);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}

	// get all users list
	@Test
	public void testListAllUsers() {

		Iterable<User> listUsers = repo.findAll();
		listUsers.forEach(user -> System.out.println(user));
	}

	// get user by its id
	@Test
	public void testGetUserById() {
		User userKiran = repo.findById(1).get();
		System.out.println(userKiran);
		assertThat(userKiran).isNotNull();

	}

	// update user details
	@Test
	public void testUpdateUserDetails() {
		User userKiran = repo.findById(1).get();

		userKiran.setEnabled(true);
		userKiran.setEmail("kiransingbhat1998@gmail.com");

		repo.save(userKiran);
	}

	// update users role
	@Test
	public void testUpdateUserRoles() {
		User userManish = repo.findById(2).get();

		Role roleEditor = new Role(3);
		Role roleSalesperson = new Role(2);

		userManish.getRoles().remove(roleEditor);
		userManish.addRole(roleSalesperson);

		repo.save(userManish);

	}

	// delete user by id id
	@Test
	public void testDeleteUser() {

		Integer userId = 2;

		repo.deleteById(userId);

	}

	@Test
	public void testGetUserByEmail() {

		String email = "abssc@def.com";

		User user = repo.getUserByEmail(email);

		System.out.println(user);

		assertThat(user).isNotNull();

	}

	@Test
	public void testCountById() {
		Integer id = 1;

		Long countById = repo.countById(id);

		assertThat(countById).isNotNull().isGreaterThan(0);

	}

	@Test
	public void testDisabledUser() {
		Integer id = 2;

		repo.updateEnabledStatus(id, false);
	}

	@Test
	public void testEnabledUser() {
		Integer id = 1;

		repo.updateEnabledStatus(id, true);
	}

	@Test
	public void testListFirstPahe() {
		int pageNumber = 2;
		int pageSize = 4;
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repo.findAll(pageable);

		List<User> listUsers = page.getContent();
		listUsers.forEach(user -> System.out.println(user));

		assertThat(listUsers.size()).isEqualTo(pageSize);

	}

}
