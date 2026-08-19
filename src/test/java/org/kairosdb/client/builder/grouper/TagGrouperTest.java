package org.kairosdb.client.builder.grouper;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TagGrouperTest {

	@Test
	void test_constructor_nullTagNames_invalid() {
		assertThrows(NullPointerException.class, () -> new TagGrouper((String[]) null));
	}

	@Test
	void test_constructor_nullTagName_invalid() {
		assertThrows(NullPointerException.class, () -> new TagGrouper("tag1", null));
	}

	@Test
	void test_constructor_emptyTagNameList_invalid() {
		assertThrows(IllegalArgumentException.class, TagGrouper::new);
	}

	@Test
	void test_constructor_null_list_invalid() {
		assertThrows(NullPointerException.class, () -> new TagGrouper((List<String>) null));
	}

	@Test
	void test_constructor_name() { assertEquals("tag", new TagGrouper("tag1", "tag2").getName()); }

	@Test
	void test_constructor_list_name() { assertEquals("tag", new TagGrouper(Arrays.asList("tag1", "tag2")).getName()); }

	@Test
	void test_constructor_tagNames() { assertThat(new TagGrouper("tag1", "tag2").getTagNames()).containsExactlyInAnyOrder("tag1", "tag2"); }

	@Test
	void test_constructor_tagNames_from_list() { assertThat(new TagGrouper(Arrays.asList("tag1", "tag2")).getTagNames()).containsExactlyInAnyOrder("tag1", "tag2"); }
}