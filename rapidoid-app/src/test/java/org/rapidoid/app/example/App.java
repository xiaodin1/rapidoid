package org.rapidoid.app.example;

/*
 * #%L
 * rapidoid-app
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

public class App {

	String title = "Task Planer";

	boolean search = true;

	boolean themes = true;

	boolean fluid = true;

	boolean settings = true;

	Object[] screens = { HomeScreen.class, NewTaskScreen.class, TasksScreen.class };

}
