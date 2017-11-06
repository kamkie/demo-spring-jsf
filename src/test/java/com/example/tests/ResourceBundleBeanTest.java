package com.example.tests;

import com.example.component.ResourceBundleBean;
import com.example.view.LocaleModel;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceBundleBeanTest {
    @SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
    @Test
    public void equals() throws Exception {
        ResourceBundleBean resourceBundleBean = new ResourceBundleBean(null, null);

        assertThat(resourceBundleBean).isEqualTo(resourceBundleBean)
                .isEqualTo(new ResourceBundleBean(null, null));
    }

    @SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
    @Test
    public void notEquals() throws Exception {
        assertThat(new ResourceBundleBean(new LocaleModel(null), null))
                .isNotEqualTo(new ResourceBundleBean(null, null))
                .isNotEqualTo(null)
                .isNotEqualTo(new HashMap<>());
    }

}
