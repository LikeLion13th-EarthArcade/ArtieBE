package com.project.team5backend.global.util;

import java.util.function.Consumer;

public class UpdateUtils {

    // 바꿀 값, 기존 값을 비교해서 바꿀 값이 기존 값과 다르다면, updater를 통해 변경하는 제네릭 메서드입니다
    public static <T> void updateIfChanged(T newValue, T oldValue, Consumer<T> updater) {
        if (newValue != null && !newValue.equals(oldValue)) {
            updater.accept(newValue);
        }
    }
}
