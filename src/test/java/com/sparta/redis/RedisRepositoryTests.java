package com.sparta.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisRepositoryTests {
    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void createTest() {
        // 객체를 만들고
        Item item = Item.builder()
                .name("keyboard")
                .description("Mechanical Keyboard Expensive 😢")
                .price(100000)
                .build();
        // save를 호출한다.
        itemRepository.save(item);
    }

    @Test
    public void readOneTest() {
        Item item = itemRepository.findById("")
                .orElseThrow();
        System.out.println(item.getDescription());
    }


    @Test
    public void updateTest() {
        Item item = itemRepository.findById("")
                .orElseThrow();
        item.setDescription("On Sale!!!");
        itemRepository.save(item);

        item = itemRepository.findById("")
                .orElseThrow();
        System.out.println(item.getDescription());
    }

    @Test
    public void deleteTest() {
        itemRepository.deleteById("");
    }
}