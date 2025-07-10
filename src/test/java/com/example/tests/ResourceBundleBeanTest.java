package com.example.tests;

import com.example.component.ResourceBundleBean;
import com.example.viewmodel.LocaleModel;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceBundleBeanTest {

    @Test
    @SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
    void testEquals() {
        ResourceBundleBean resourceBundleBean = new ResourceBundleBean(null, null);

        assertThat(resourceBundleBean).isEqualTo(resourceBundleBean);
    }

    @Test
    @SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
    void testEntrySet() {
        ResourceBundleBean resourceBundleBean = new ResourceBundleBean(null, null);

        assertThat(resourceBundleBean.entrySet()).hasSize(0);
    }

    @Test
    @SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
    void testNotEquals() {
        assertThat(new ResourceBundleBean(new LocaleModel(null, null), null))
                .isNotEqualTo(new ResourceBundleBean(null, null))
                .isNotEqualTo(null)
                .isNotEqualTo(new HashMap<>());
    }

}
