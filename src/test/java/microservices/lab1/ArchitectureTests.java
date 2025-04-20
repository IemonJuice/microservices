package microservices.lab1;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class ArchitectureTests {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter().importPackages("microservices.lab1");
    }

    @Test
    void controllersShouldOnlyBeInControllersPackage() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(RestController.class)
                .should().resideInAnyPackage(
                        "microservices.lab1.user.controllers",
                        "microservices.lab1.media.controllers",
                        "microservices.lab1.music.controllers",
                        "microservices.lab1.video.controllers"
                );
        rule.check(importedClasses);
    }

    @Test
    void servicesShouldOnlyBeInServicesPackage() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Service.class)
                .should().resideInAnyPackage(
                        "microservices.lab1.user.services",
                        "microservices.lab1.music.services",
                        "microservices.lab1.video.services"
                );
        rule.check(importedClasses);
    }

    @Test
    void repositoriesShouldOnlyBeInRepositoryPackage() {
        ArchRule rule = classes()
                .that().resideInAPackage("microservices.lab1.user.repository")
                .should().haveSimpleNameEndingWith("Repository");
        rule.check(importedClasses);
    }

    @Test
    void dtosShouldOnlyBeInDtoPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("DTO")
                .should().resideInAnyPackage(
                        "microservices.lab1.user.dto",
                        "microservices.lab1.music.dto",
                        "microservices.lab1.video.dto"
                );
        rule.check(importedClasses);
    }

    @Test
    void controllersShouldNotDependOnRepositories() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("microservices.lab1.user.controllers")
                .should().dependOnClassesThat()
                .resideInAPackage("microservices.lab1.user.repository");
        rule.check(importedClasses);
    }

    @Test
    void servicesShouldNotDependOnControllers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("microservices.lab1.user.services")
                .should().dependOnClassesThat()
                .resideInAPackage("microservices.lab1.user.controllers");
        rule.check(importedClasses);
    }

    @Test
    void mappersShouldNotDependOnControllers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("microservices.lab1.user.mapper")
                .should().dependOnClassesThat()
                .resideInAPackage("microservices.lab1.user.controllers");
        rule.check(importedClasses);
    }

    @Test
    void controllerClassesShouldBeNamedProperly() {
        ArchRule rule = classes()
                .that().resideInAPackage("microservices.lab1.user.controllers")
                .should().haveSimpleNameEndingWith("Controller");
        rule.check(importedClasses);
    }

    @Test
    void serviceClassesShouldBeNamedProperly() {
        ArchRule rule = classes()
                .that().resideInAPackage("microservices.lab1.user.services")
                .should().haveSimpleNameEndingWith("Service");
        rule.check(importedClasses);
    }

    @Test
    void repositoryClassesShouldBeNamedProperly() {
        ArchRule rule = classes()
                .that().resideInAPackage("microservices.lab1.user.repository")
                .should().haveSimpleNameEndingWith("Repository");
        rule.check(importedClasses);
    }

    @Test
    void mappersShouldBeAnnotatedWithComponent() {
        ArchRule rule = classes()
                .that().resideInAPackage("microservices.lab1.user.mapper")
                .should().beAnnotatedWith(Component.class);
        rule.check(importedClasses);
    }

    @Test
    void servicesShouldBeAnnotatedWithService() {
        ArchRule rule = classes()
                .that().resideInAPackage("microservices.lab1.user.services")
                .should().beAnnotatedWith(Service.class);
        rule.check(importedClasses);
    }

    @Test
    void repositoriesShouldOnlyBeAccessedByServicesAndMappers() {
        ArchRule rule = classes()
                .that().resideInAPackage("microservices.lab1.user.repository")
                .should().onlyBeAccessed().byAnyPackage(
                        "microservices.lab1.user.services",
                        "microservices.lab1.user.mapper",
                        "microservices.lab1.user.repository"
                );
        rule.check(importedClasses);
    }

    @Test
    void modelsShouldOnlyBeAccessedBySpecificPackages() {
        ArchRule rule = classes()
                .that().resideInAPackage("microservices.lab1.user.models")
                .should().onlyBeAccessed().byAnyPackage(
                        "microservices.lab1.user.services",
                        "microservices.lab1.user.mapper",
                        "microservices.lab1.user.repository",
                        "microservices.lab1.user.models",
                        "microservices.lab1.music.services",
                        "microservices.lab1.video.mapper",
                        "microservices.lab1.video.services"
                );
        rule.check(importedClasses);
    }

    @Test
    void noClassesShouldResideInRootPackage() {
        ArchRule rule = classes()
                .that().resideInAPackage("microservices.lab1")
                .should().haveSimpleNameEndingWith("Application");
    }

    @Test
    void dtoClassesShouldHaveGettersAndSettersMethods() {
        importedClasses.stream()
                .filter(c -> c.getPackageName().equals("microservices.lab1.user.dto"))
                .forEach(clazz -> {
                    boolean hasGetter = clazz.getMethods().stream()
                            .anyMatch(m -> m.getName().startsWith("get"));
                    boolean hasSetter = clazz.getMethods().stream()
                            .anyMatch(m -> m.getName().startsWith("set"));

                    if (!hasGetter || !hasSetter) {
                        throw new AssertionError("Class " + clazz.getName() + " should have getter and setter methods (likely via Lombok)");
                    }
                });
    }

    @Test
    void servicesShouldThrowCustomException() {
        ArchRule rule = classes()
                .that().resideInAPackage("microservices.lab1.user.services")
                .should().dependOnClassesThat()
                .areAssignableTo(microservices.lab1.common.exception.CustomException.class);
        rule.check(importedClasses);
    }

    @Test
    void controllersShouldHaveRestMapping() {
        ArchRule rule = classes()
                .that().resideInAPackage("microservices.lab1.user.controllers")
                .should().beAnnotatedWith(org.springframework.web.bind.annotation.RequestMapping.class);
        rule.check(importedClasses);
    }

    @Test
    void mappersShouldBeProperlyLocatedAndNamed() {
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("Mapper")
                .should().resideInAPackage("..mapper..");
        rule.check(importedClasses);
    }

    @Test
    void dtoClassesShouldNotDependOnModels() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..dto..")
                .should().dependOnClassesThat()
                .resideInAPackage("..models..");
        rule.check(importedClasses);
    }

}