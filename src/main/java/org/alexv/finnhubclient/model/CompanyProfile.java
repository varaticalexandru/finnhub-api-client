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
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "country", "currency", "exchange", "name", "ticker", "ipo", "marketCapitalization",
		"shareOutstanding", "logo", "phone", "weburl", "finnhubIndustry" })
@Data
@NoArgsConstructor
public class CompanyProfile {

	@JsonProperty("country")
	private String country;
	@JsonProperty("currency")
	private String currency;
	@JsonProperty("exchange")
	private String exchange;
	@JsonProperty("ipo")
	private String ipo;
	@JsonProperty("marketCapitalization")
	private Integer marketCapitalization;
	@JsonProperty("name")
	private String name;
	@JsonProperty("shareOutstanding")
	private Double shareOutstanding;
	@JsonProperty("ticker")
	private String ticker;
	@JsonProperty("weburl")
	private String weburl;
	@JsonProperty("logo")
	private String logo;
	@JsonProperty("finnhubIndustry")
	private String finnhubIndustry;
	@JsonProperty("phone")
	private String phone;

}
