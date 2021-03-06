package jtest;

import static jtest.TestUtil.createNewApp;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * https://stackoverflow.com/a/53145466/234110
 * 
 * @author anand
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@Import({ JTestInit.class })
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD) //Clear DB after every method
public class AppRepoTest {
	private final MediaType JSON_HAL = new MediaType("application", "hal+json", Charset.forName("UTF-8"));
	private final String APP_NAME = "test1";
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testFindById() throws Exception {
		createNewApp(mockMvc, APP_NAME);
		mockMvc.perform(get("/app")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(JSON_HAL))
				.andExpect(jsonPath("$._embedded.apps[0].appName", is(APP_NAME)));
	}
	@Test
	public void testFindAll() throws Exception {
		//Size should be 0
		mockMvc.perform(get("/app")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(JSON_HAL)).andExpect(jsonPath("$._embedded.apps", hasSize(0)));
		createNewApp(mockMvc, APP_NAME);
		//Size should be 1
		mockMvc.perform(get("/app")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(JSON_HAL)).andExpect(jsonPath("$._embedded.apps", hasSize(1)));
	}
	@Test
	public void testFindByName() throws Exception {
		mockMvc.perform(get("/app/search/by?name=jrvite")).andDo(print()).andExpect(status().isOk());
	}
}
