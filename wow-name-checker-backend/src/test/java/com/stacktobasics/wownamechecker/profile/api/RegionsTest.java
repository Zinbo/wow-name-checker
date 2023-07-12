package com.stacktobasics.wownamechecker.profile.api;

import com.stacktobasics.wownamechecker.profile.domain.Regions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

class RegionsTest {

    public static Stream<Arguments> differentRegionsSource() {
        return Stream.of(
                Arguments.of("eu", "eu"),
                Arguments.of("europe", "eu"),
                Arguments.of("us", "us"),
                Arguments.of("americas", "us")
        );
    }

    @ParameterizedTest
    @MethodSource("differentRegionsSource")
    @DisplayName("getIdFromName with region Ids and names returns region id")
    public void getIdFromNameWithKeyTest(String region, String expected) {
        // arrange
        // act
        Optional<String> actual = Regions.getIdFromName(region);

        // assert
        Assertions.assertThat(actual).contains(expected);
    }

    @Test
    @DisplayName("getIdFromName with unknown region returns empty")
    public void getIdFromNameTest() {
        // arrange
        // act
        Optional<String> actual = Regions.getIdFromName("unknown");

        // assert
        Assertions.assertThat(actual).isEmpty();
    }
}