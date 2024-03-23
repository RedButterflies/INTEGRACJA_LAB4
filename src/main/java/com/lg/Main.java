package com.lg;

import jakarta.persistence.*;

import java.io.File;
import java.nio.file.Files;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("Hibernate_JPA");
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            User u1 = new User("test_1","test_1","Andrzej", "Kowalski",Sex.MALE);
            entityManager.persist(u1);

            // Persist 5 objektow User
            for (int i = 0; i < 3; i++) {
                User user = new User("login" + i, "password" + i, "Janina" + i, "Kowalska" + i, Sex.FEMALE);
                entityManager.persist(user);
            }
            for (int i = 3; i < 5; i++) {
                User user = new User("login" + i, "password" + i, "Zdzislaw" + i, "Kokosza" + i, Sex.MALE);
                entityManager.persist(user);
            }

            // Persist 5 obiektow Role
            for (int i = 0; i < 5; i++) {
                Role role = new Role("Role" + i);
                entityManager.persist(role);
            }

            transaction.commit();

            // Update hasla dla uzytkownika z id=1
            transaction.begin();
            User userToUpdate = entityManager.find(User.class, 1L);
            if (userToUpdate != null) {
                userToUpdate.setPassword("newPassword");
                entityManager.merge(userToUpdate);
            }
            transaction.commit();

            // Usuniecie roli z id=5
            transaction.begin();
            Role roleToDelete = entityManager.find(Role.class, 5L);
            if (roleToDelete != null) {
                entityManager.remove(roleToDelete);
            }
            transaction.commit();

            // JPQL query do znalezienia wszystkich uzytkownikow o nazwisku "Kowalski"
            transaction.begin();
            Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.lastName = 'Kowalski'");
            List<User> kowalscy = query.getResultList();
            transaction.commit();

            System.out.println("Użytkownicy o nazwisku Kowalski:");
            for (User user : kowalscy) {
                System.out.println(user);
            }

            // JPQL query  do znaleznienia wszystkich kobiet
            transaction.begin();
            Query query1 = entityManager.createQuery("SELECT u FROM User u WHERE u.sex = 'FEMALE'");
            List<User> kobiety = query1.getResultList();
            transaction.commit();

            System.out.println("Lista kobiet w bazie danych:");
            for (User kobieta : kobiety) {
                System.out.println(kobieta);
            }

            transaction.begin();

            // Stworzenie nowego uzytkownika
            User user = new User("login", "password", "John", "Smithowsky", Sex.MALE);

            // Dodanie dwoch rol do uzytkownika
            Role role1 = new Role("ROLE_ADMIN");
            Role role2 = new Role("ROLE_USER");

            user.addRole(role1);
            user.addRole(role2);

            entityManager.persist(user);
            transaction.commit();

            transaction.begin();

            // Tworzenie nowych grup
            UsersGroup group1 = new UsersGroup();
            UsersGroup group2 = new UsersGroup();

            // Tworzenie nowych uzytkownikow
            User user1 = new User("loginA", "password1", "John", "Doe", Sex.MALE);
            User user2 = new User("loginB", "password2", "Alice", "Smith", Sex.FEMALE);
            User user3 = new User("loginC", "password3", "Bob", "Brown", Sex.MALE);

            // Dodanie uzytkownikow do grup za pomoca metody addToGroup
            user1.addToGroup(group1);
            user2.addToGroup(group1);
            user2.addToGroup(group2);
            user3.addToGroup(group2);

            // Persist group i uzytkownikow
            entityManager.persist(group1);
            entityManager.persist(group2);
            entityManager.persist(user1);
            entityManager.persist(user2);
            entityManager.persist(user3);
            transaction.commit();

            transaction.begin();


        // Wczytanie obrazka i konwersja na tablicę bajtów
            File file = new File("src/kwiatek.png");
            byte[] imageData = Files.readAllBytes(file.toPath());

        // Tworzenie nowego użytkownika
            User userA = new User("loginS", "password", "Kwiatek", "Bratek", Sex.MALE, imageData);

        // Zapis użytkownika do bazy danych
            entityManager.persist(userA);
            transaction.commit();

            System.out.println("Użytkownik został poprawnie zapisany z obrazkiem.");

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            entityManager.close();
            factory.close();
        }
    }
}


