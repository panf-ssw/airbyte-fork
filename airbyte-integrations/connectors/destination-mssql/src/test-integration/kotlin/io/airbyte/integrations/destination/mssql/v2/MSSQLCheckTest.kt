/*
 * Copyright (c) 2024 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.integrations.destination.mssql.v2

import io.airbyte.cdk.command.FeatureFlag
import io.airbyte.cdk.load.check.CheckIntegrationTest
import io.airbyte.cdk.load.check.CheckTestConfig
import io.airbyte.integrations.destination.mssql.v2.config.MSSQLSpecification
import java.nio.file.Files
import org.junit.jupiter.api.BeforeAll

internal class MSSQLCheckTest :
    CheckIntegrationTest<MSSQLSpecification>(
        successConfigFilenames =
            listOf(
                CheckTestConfig(
                    Files.readString(MSSQLTestConfigUtil.getConfigPath("check/valid.json")),
                    name = "Unencrypted connection should work for OSS",
                ),
                CheckTestConfig(
                    Files.readString(
                        MSSQLTestConfigUtil.getConfigPath("check/valid-ssl-trust.json")
                    ),
                    name = "SSL Thrust should work for OSS",
                ),
                CheckTestConfig(
                    Files.readString(
                        MSSQLTestConfigUtil.getConfigPath("check/valid-ssl-trust.json")
                    ),
                    setOf(FeatureFlag.AIRBYTE_CLOUD_DEPLOYMENT),
                    name = "SSL Thrust should work for Cloud",
                ),
            ),
        failConfigFilenamesAndFailureReasons =
            mapOf(
                CheckTestConfig(
                    Files.readString(MSSQLTestConfigUtil.getConfigPath("check/valid.json")),
                    setOf(FeatureFlag.AIRBYTE_CLOUD_DEPLOYMENT),
                    name = "Unencrypted is not supported in Cloud"
                ) to "Airbyte Cloud requires SSL encryption".toPattern(),
                CheckTestConfig(
                    Files.readString(
                        MSSQLTestConfigUtil.getConfigPath("check/fail-database-invalid.json")
                    ),
                    name = "Invalid database name",
                ) to "Login failed for user 'sa'".toPattern(),
            ),
        configUpdater = MSSQLConfigUpdater()
    ) {

    companion object {
        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            MSSQLContainerHelper.start()
        }
    }
}
