![ONIX Labs](https://raw.githubusercontent.com/onix-labs/onix-labs.github.io/master/content/logo/master_full_md.png)

# ONIXLabs Corda Core

ONIXLabs Corda Core provides Corda developers with a suite of APIs to empower development of Corda contracts, workflows and integration. These APIs are not intended to be used as CorDapps, since they do not contain any states or contracts, however they are partitioned into contract, workflow and integration modules containing features to improve and simplify CorDapp design and development.

## CorDapp Signing

As of version 2.0.0 of the ONIXLabs Corda Core API, this repository ships with the ONIXLabs developer signing key so that this repository can be checked out and built locally. Official releases of this API are signed with the ONIXLabs production signing key.

## Integration Guide

Follow these steps to integrate the ONIXLabs Corda Core API into your application

1.  Add the following to your project's top level **build.gradle** file.

    ```
    buildscript {
        ext {
            onixlabs_group = 'io.onixlabs'
            onixlabs_corda_core_release_version = '2.0.0'
        }
    }
    ```

2.  Implement the ONIXLabs Corda Core contract module into your contract.

    ```
    dependencies {
        cordapp "$onixlabs_group:onixlabs-corda-core-contract:$onixlabs_corda_core_release_version"
    }
    ```

3.  Implement the ONIXLabs Corda Core workflow module into your workflow.

    ```
    dependencies {
        cordapp "$onixlabs_group:onixlabs-corda-core-workflow:$onixlabs_corda_core_release_version"
    }
    ```

4.  Implement the ONIXLabs Corda Core integration module into your integration.

    ```
    dependencies {
        implementation "$onixlabs_group:onixlabs-corda-core-integration:$onixlabs_corda_core_release_version"
    }
    ```





