package assign2package.TestCases;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import assign2package.DBQueries;

class LoginTests {

		DBQueries dbqueries = new DBQueries();
		
	@Test
	void testLoginGoodUsernameAndPassword() {
		boolean result = dbqueries.login("Oliver", "12345678");
		assertEquals(true, result);
	}
	
	@Test
	void testLoginBadUsernameButGoodPassword() {
		boolean result = dbqueries.login("NotAUser", "12345678");
		assertEquals(false, result);
	}
	
	@Test
	void testLoginGoodUsernameButBadPassword() {
		boolean result = dbqueries.login("Oliver", "11111111");
		assertEquals(false, result);
	}
	
	@Test
	void testLoginBadUsernameAndBadPassword() {
		boolean result = dbqueries.login("NotAUser", "11111111");
		assertEquals(false, result);
	}
	
	@Test
	void testLoginBlankUsernameAndPassword() {
		boolean result = dbqueries.login("", "");
		assertEquals(false, result);
	}
	
	@Test
	void testLoginTooManyCharacters() {
		boolean result = dbqueries.login("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "1111111111111111111111111111111111111");
		assertEquals(false, result);
	}
	
	@Test
	void testLoginInvalidCharacters() {
		boolean result = dbqueries.login("1111111", "aaaaa");
		assertEquals(false, result);
	}

}
