package com.example.demo.services;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findUsersByEmail(username)
                .orElseThrow(()->new UsernameNotFoundException("Username not found with username: " + username));

        return build(user);
    }

    public User loadUserById(Long id){
        return userRepository.findUsersById(id).orElse(null);
    }

    public static User build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name())).
                collect(Collectors.toList());

        return new User(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities);
    }


}
/*
    Цей клас `CustomUserDetailsService` є реалізацією інтерфейсу `UserDetailsService` з пакету Spring Security.
    Він відповідає за завантаження інформації про користувача (наприклад, інформація про користувача,
    його ролі та дозволи) з бази даних або іншого джерела даних під час процесу аутентифікації
    користувача в Spring Security.

        Основна задача цього класу полягає в тому, щоб знайти користувача за його іменем користувача (у вашому випадку,
         за email) і повернути об'єкт, який реалізує інтерфейс `UserDetails`.
          Цей об'єкт `UserDetails` містить інформацію про користувача, його ролі та дозволи,
           які будуть використовуватися під час процесу аутентифікації та авторизації.

        Давайте розглянемо методи та їх функції в цьому класі:

        1. Конструктор `CustomUserDetailsService`:
        - Цей конструктор приймає об'єкт `UserRepository` в якості параметра через внедрення залежності (`@Autowired`).
        - `UserRepository` використовується для звернення до бази даних і отримання інформації про користувачів.

        2. Метод `loadUserByUsername(String username)`:
        - Цей метод обов'язковий для імплементації з інтерфейсу `UserDetailsService`.
        - Він приймає ім'я користувача (у вашому випадку, email) як параметр і повертає об'єкт `UserDetails`.
        - Метод спершу використовує `UserRepository`, щоб знайти користувача за його іменем користувача (email).
        - Якщо користувач не знайдений, генерується виняток `UsernameNotFoundException`.
        - В іншому випадку викликається статичний метод `build`, який створює об'єкт `User` із знайденим
         користувачем та його ролями та дозволами, і цей об'єкт повертається як результат.

        3. Статичний метод `build(User user)`:
        - Цей метод приймає об'єкт `User`, який представляє користувача.
        - Використовуючи `Stream API`, він створює список `GrantedAuthority`, який представляє ролі
         користувача як об'єкти `SimpleGrantedAuthority`. Ці ролі витягуються з об'єкта `User` та додаються до списку.
        - Після цього метод створює новий об'єкт `User`, який реалізує інтерфейс `UserDetails`
        і містить інформацію про користувача, його ім'я, пароль і список ролей.
        - Отриманий об'єкт `User` повертається як результат.

        Коли користувач буде аутентифікований, Spring Security буде використовувати об'єкт `UserDetails`,
         створений в цьому класі, для проведення авторизації та доступу до ресурсів в залежності від ролей
          та дозволів користувача.

 */