/*-
 * #%L
 * rapidoid-networking
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
 * %%
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
 * #L%
 */

package org.rapidoid.net;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.BasicConfig;
import org.rapidoid.config.Conf;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class TCP extends RapidoidThing {

	public static ServerBuilder server() {
		return new ServerBuilder(Conf.NET);
	}

	public static ServerBuilder server(BasicConfig cfg) {
		return new ServerBuilder(cfg.or(Conf.NET));
	}

	public static TCPClientBuilder client() {
		return new TCPClientBuilder(Conf.NET);
	}

	public static TCPClientBuilder client(BasicConfig cfg) {
		return new TCPClientBuilder(cfg.or(Conf.NET));
	}

	public static TCPClient connect(String host, int port, Protocol protocol) {
		TCPClient client = client().host(host).port(port).protocol(protocol).build();
		client.start();
		return client;
	}

}
