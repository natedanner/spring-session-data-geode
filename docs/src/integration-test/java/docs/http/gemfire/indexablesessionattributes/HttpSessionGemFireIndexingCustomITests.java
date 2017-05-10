/*
 * Copyright 2014-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package docs.http.gemfire.indexablesessionattributes;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.config.annotation.PeerCacheApplication;
import org.springframework.session.ExpiringSession;
import org.springframework.session.data.gemfire.GemFireOperationsSessionRepository;
import org.springframework.session.data.gemfire.config.annotation.web.http.EnableGemFireHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Rob Winch
 * @author John Blum
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = HttpSessionGemFireIndexingCustomITests.Config.class)
public class HttpSessionGemFireIndexingCustomITests {

	@Autowired
	private GemFireOperationsSessionRepository sessionRepository;

	@Test
	public void findByIndexName() {
		ExpiringSession session = this.sessionRepository.createSession();
		String attrValue = "HttpSessionGemFireIndexingCustomITests-findByIndexName";

		// tag::findbyindexname-set[]
		String indexName = "name1";
		session.setAttribute(indexName, attrValue);
		// end::findbyindexname-set[]

		this.sessionRepository.save(session);

		// tag::findbyindexname-get[]
		Map<String, ExpiringSession> idToSessions =
			this.sessionRepository.findByIndexNameAndIndexValue(indexName, attrValue);
		// end::findbyindexname-get[]

		assertThat(idToSessions.keySet()).containsOnly(session.getId());

		this.sessionRepository.delete(session.getId());
	}

	@PeerCacheApplication(name = "HttpSessionGemFireIndexingCustomITests", logLevel = "error")
	@EnableGemFireHttpSession(indexableSessionAttributes = { "name1", "name2", "name3" },
		regionName = "HttpSessionGemFireIndexingCustomTestRegion")
	static class Config {
	}
}
