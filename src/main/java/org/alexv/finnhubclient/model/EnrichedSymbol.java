/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alexv.finnhubclient.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "currency", "figi", "mic", "isin", "shareClassFIGI", "symbol2" })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrichedSymbol extends Symbol {

	@JsonProperty("currency")
	private String currency;
	@JsonProperty("figi")
	private String figi;
	@JsonProperty("mic")
	private String mic;
	@JsonProperty("isin")
	private String isin;
	@JsonProperty("shareClassFIGI")
	private String shareClassFIGI;
	@JsonProperty("symbol2")
	private String symbol2;
}
