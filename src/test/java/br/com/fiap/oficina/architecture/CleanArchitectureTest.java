package br.com.fiap.oficina.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "br.com.fiap.oficina", importOptions = ImportOption.DoNotIncludeTests.class)
class CleanArchitectureTest {
    @ArchTest
    static final ArchRule coreIsIndependent = noClasses().that()
            .resideInAnyPackage("..domain..", "..application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..frameworks..", "..interfaceadapters..", "org.springframework..", "jakarta.persistence..",
                    "org.slf4j..", "datadog..", "com.datadog..", "io.opentelemetry..");

    @ArchTest
    static final ArchRule domainDoesNotDependOnApplication = noClasses().that()
            .resideInAPackage("..domain..").should().dependOnClassesThat().resideInAPackage("..application..");

    @ArchTest
    static final ArchRule sharedDoesNotKnowOrderModule = noClasses().that()
            .resideInAPackage("..shared..").should().dependOnClassesThat().resideInAPackage("..ordemservico..");

    @ArchTest
    static final ArchRule logPublisherDoesNotKnowInfrastructure = noClasses().that()
            .resideInAPackage("..ordemservico.interfaceadapters.observability..")
            .should().dependOnClassesThat().resideInAnyPackage("..frameworks..", "jakarta.persistence..", "org.springframework..");
}
