package com.example.tests;

import com.example.component.ResourceBundleBean;
import com.example.viewmodel.LocaleModel;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceBundleBeanTest {

    @Test
    void testEquals() {
        ResourceBundleBean resourceBundleBean = new ResourceBundleBean(null, null);

        assertThat(resourceBundleBean).isEqualTo(resourceBundleBean);
    }

    @Test
    void testEntrySet() {
        ResourceBundleBean resourceBundleBean = new ResourceBundleBean(null, null);

        assertThat(resourceBundleBean.entrySet()).hasSize(0);
    }

    @Test
    void testNotEquals() {
        assertThat(new ResourceBundleBean(new LocaleModel(null, null), null))
                .isNotEqualTo(new ResourceBundleBean(null, null))
                .isNotEqualTo(null)
                .isNotEqualTo(new HashMap<>());
    }

}
