Line based comparison of the difference between 
the RNG grammar of the schema part of ODF 1.2 and ODF 1.3:

Latest ODF 1.2: OpenDocument-v1.2-os-schema.rng
http://docs.oasis-open.org/office/v1.2/os/OpenDocument-v1.2-os-schema.rng

A sorted ODF 1.2 RNG file can be found at:
http://tools.oasis-open.org/version-control/browse/wsvn/office/trunk/v1.2/OpenDocument-schema-v1.2.rng

Latest ODF 1.3: OpenDocument-v1.3-cs01-schema.rng
http://docs.oasis-open.org/office/OpenDocument/v1.3/cs01/schemas/OpenDocument-schema-v1.3.rng


Obviously the sorted ODF 1.2 RNG above was sorted (normalized) by the Java Utility called OdfHistory:
https://gitlab.com/odfplugfest/odfhistory
(to build apply my patch: https://gitlab.com/odfplugfest/odfhistory/-/merge_requests/2)
Run the command line command:
"java -jar <input_RNG> <output_RNG>

To verify that the sorted SVN file has the same feature set as the original ODF 1.2, a very similar XSL transformation was created:
 MISSING LINK

The following steps were done to successfully  validate that the SVN file as the same feature set as the original ODF 1.2 schema file:

Step 0: The original ODF 1.2 RNG schema file was sorted by OdfHistory to
			xslt-runner/src/test/resources/odf12/rng-normalization/OpenDocument-v1.2-os-schema_sorted_by_odfhistory.rng
		
Step 1: Before the XSLT was triggered some whitespace noise was manally removed:
		Removing 18 empty lines within the ODF 1.2
			xslt-runner/src/test/resources/odf12/rng-normalization/OpenDocument-v1.2-os-schema_original_but_manual_space_adoption.rng
		
Step 2: Running the XSLT with command line in the xslt-runner project: "mvn clean install"
		Creating: xslt-runner/target/generated-resources/xml/xslt/OpenDocument-v1.2-os-schema_original_but_manual_space_adoption.xml
		This should be similar as the existing: 
			xslt-runner/src/test/resources/odf12/rng-normalization/OpenDocument-v1.2-os-schema_original_but_manual_space_adoption_XSLT-OUPUT_SORTED.rng
		
Step 3: Both identical files from Step 2 show a lot of changes to the sorted file by OdfHistory, as the latter is adding the rng: prefix.
		Therefore all "<rng:" element prefixes of the file in step 0 created by OdfHistory are being replaced with "<", resulting into the file:
			xslt-runner/src/test/resources/odf12/rng-normalization/OpenDocument-v1.2-os-schema_sorted_by_odfhistory_manualAdopted.rng
		
Step 4: Comparing
			xslt-runner/src/test/resources/odf12/rng-normalization/OpenDocument-v1.2-os-schema_sorted_by_odfhistory_manualAdopted.rng
		with
			xslt-runner/src/test/resources/odf12/rng-normalization/OpenDocument-v1.2-os-schema_original_but_manual_space_adoption_XSLT-OUPUT_SORTED.rng
		show only differences in the namespace declarations, which were sorted by OdfHistory as well.