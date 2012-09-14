/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.jutzig.jabylon.users.tests;

import de.jutzig.jabylon.users.Permission;
import de.jutzig.jabylon.users.Role;
import de.jutzig.jabylon.users.UsersFactory;

import junit.framework.TestCase;

import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Role</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following operations are tested:
 * <ul>
 *   <li>{@link de.jutzig.jabylon.users.Role#getAllPermissions() <em>Get All Permissions</em>}</li>
 * </ul>
 * </p>
 * @generated
 */
public class RoleTest extends TestCase {

	/**
	 * The fixture for this Role test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Role fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(RoleTest.class);
	}

	/**
	 * Constructs a new Role test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RoleTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Role test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(Role fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Role test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Role getFixture() {
		return fixture;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated
	 */
	@Override
	protected void setUp() throws Exception {
		setFixture(UsersFactory.eINSTANCE.createRole());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#tearDown()
	 * @generated
	 */
	@Override
	protected void tearDown() throws Exception {
		setFixture(null);
	}

	/**
	 * Tests the '{@link de.jutzig.jabylon.users.Role#getAllPermissions() <em>Get All Permissions</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see de.jutzig.jabylon.users.Role#getAllPermissions()
	 * @generated NOT
	 */
	public void testGetAllPermissions() {
		assertEquals(0, getFixture().getAllPermissions().size());
	}
	
	public void testGetAllPermissionsNoParent() {
		Permission permission = UsersFactory.eINSTANCE.createPermission();
		getFixture().getPermissions().add(permission);
		assertEquals(1, getFixture().getAllPermissions().size());
		assertSame(permission, getFixture().getAllPermissions().get(0));
	}
	
	public void testGetAllPermissionsParent() {
		Permission permission = UsersFactory.eINSTANCE.createPermission();
		Role parent = UsersFactory.eINSTANCE.createRole();
		getFixture().setParent(parent);
		parent.getPermissions().add(permission);
		assertEquals(1, getFixture().getAllPermissions().size());
		assertSame(permission, getFixture().getAllPermissions().get(0));
	}
	
	
	public void testGetAllPermissionsMixed() {
		Permission parentPermission = UsersFactory.eINSTANCE.createPermission();
		Permission childPermission = UsersFactory.eINSTANCE.createPermission();
		Role parent = UsersFactory.eINSTANCE.createRole();
		getFixture().setParent(parent);
		getFixture().getPermissions().add(childPermission);
		parent.getPermissions().add(parentPermission);
		assertEquals(2, getFixture().getAllPermissions().size());
		assertSame(childPermission, getFixture().getAllPermissions().get(0));
		assertSame(parentPermission, getFixture().getAllPermissions().get(1));
	}

} //RoleTest
