package com.bjss.nhsd.a2si.capacityservice.persistence;

import com.bjss.nhsd.a2si.capacityinformation.domain.CapacityInformation;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test-capacity-service-local-stub")
public class CapacityServiceApplicationStubRepositoryTests {

    private static final String defaultServiceId = "defaultServiceId";

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private CapacityInformationRepositoryStubImpl capacityInformationRepository;

    @Before
    public void prePopulate() {

        capacityInformationRepository = new CapacityInformationRepositoryStubImpl();
        capacityInformationRepository.init();

        CapacityInformation capacityInformation = new CapacityInformation();
        capacityInformation.setServiceId(defaultServiceId);
        capacityInformation.setMessage(CapacityInformation.messageTemplate);
        capacityInformation.setLastUpdated(dateTimeFormatter.format(LocalDateTime.now()));

        capacityInformationRepository.saveCapacityInformation(capacityInformation);

    }

	@Test
	public void testSaveCapacityInformation() {

		CapacityInformation capacityInformation = new CapacityInformation();
		capacityInformation.setServiceId("newServiceId");
		capacityInformation.setMessage(CapacityInformation.messageTemplate);
        capacityInformation.setLastUpdated(dateTimeFormatter.format(LocalDateTime.now()));

		capacityInformationRepository.saveCapacityInformation(capacityInformation);
	}

    @Test
    public void testGetCapacityInformation() {

        CapacityInformation capacityInformation =
                capacityInformationRepository.getCapacityInformationByServiceId(defaultServiceId);
        assertEquals(capacityInformation.getServiceId(), defaultServiceId);
        assertEquals(capacityInformation.getMessage(),CapacityInformation.messageTemplate);
    }

    @Test
    public void testGetLatestCapacityInformationFromMultipleServiceIdsAndDates() {

        LocalDateTime now = LocalDateTime.now();

        CapacityInformation capacityInformation;

        String serviceId0001 = "serviceId0001";
        String serviceId0002 = "serviceId0002";


        capacityInformation = new CapacityInformation(serviceId0001, CapacityInformation.messageTemplate,
                dateTimeFormatter.format(now.minusHours(3)));

        capacityInformationRepository.saveCapacityInformation(capacityInformation);

        capacityInformation = new CapacityInformation(serviceId0001, CapacityInformation.messageTemplate,
                dateTimeFormatter.format(now.minusHours(1)));

        capacityInformationRepository.saveCapacityInformation(capacityInformation);

        capacityInformation = new CapacityInformation(serviceId0001, CapacityInformation.messageTemplate,
                dateTimeFormatter.format(now));

        capacityInformationRepository.saveCapacityInformation(capacityInformation);

        capacityInformation = new CapacityInformation(serviceId0001, CapacityInformation.messageTemplate,
                dateTimeFormatter.format(now.plusHours(1)));

        capacityInformationRepository.saveCapacityInformation(capacityInformation);

        capacityInformation = new CapacityInformation(serviceId0001, CapacityInformation.messageTemplate,
                dateTimeFormatter.format(now.plusHours(3)));

        capacityInformationRepository.saveCapacityInformation(capacityInformation);

        capacityInformation = new CapacityInformation(serviceId0002, CapacityInformation.messageTemplate,
                "2017-09-20 00:00:00");

        capacityInformationRepository.saveCapacityInformation(capacityInformation);

        capacityInformation = new CapacityInformation(serviceId0002, CapacityInformation.messageTemplate,
                "2017-10-20 20:00:00");

        capacityInformationRepository.saveCapacityInformation(capacityInformation);

        capacityInformation = new CapacityInformation(serviceId0002, CapacityInformation.messageTemplate,
                "2017-10-20 10:00:00");

        capacityInformationRepository.saveCapacityInformation(capacityInformation);

        CapacityInformation latestCapacityInformationForServiceId0001 =
                capacityInformationRepository.getCapacityInformationByServiceId(serviceId0001);
        assertEquals(CapacityInformation.messageTemplate, latestCapacityInformationForServiceId0001.getMessage());

    }

    @Test
    public void testGetLatestCapacityInformationWithOnlyFutureObjectsReturnsNull() {

        LocalDateTime inOneMonth = LocalDateTime.now().plusMonths(1);

        CapacityInformation capacityInformation;

        String serviceId0005 = "serviceId0005";

        capacityInformation = new CapacityInformation(serviceId0005, CapacityInformation.messageTemplate,
                dateTimeFormatter.format(inOneMonth));

        capacityInformationRepository.saveCapacityInformation(capacityInformation);

        capacityInformation = new CapacityInformation(serviceId0005, CapacityInformation.messageTemplate,
                dateTimeFormatter.format(inOneMonth.plusDays(1)));

        capacityInformationRepository.saveCapacityInformation(capacityInformation);

        capacityInformation = new CapacityInformation(serviceId0005, CapacityInformation.messageTemplate,
                dateTimeFormatter.format(inOneMonth.plusDays(2)));

        capacityInformationRepository.saveCapacityInformation(capacityInformation);

        CapacityInformation latestCapacityInformationForServiceId0005 =
                capacityInformationRepository.getCapacityInformationByServiceId(serviceId0005);
        assertNull(latestCapacityInformationForServiceId0005);

    }

    @Test
    public void testDeleteCapacityInformationForServiceId() {

        LocalDateTime inOneMonth = LocalDateTime.now().plusMonths(1);

        CapacityInformation capacityInformation;

        String serviceId0006 = "serviceId0006";

        capacityInformation = new CapacityInformation(serviceId0006, CapacityInformation.messageTemplate,
                dateTimeFormatter.format(inOneMonth));

        capacityInformationRepository.saveCapacityInformation(capacityInformation);

        capacityInformation = new CapacityInformation(serviceId0006, CapacityInformation.messageTemplate,
                dateTimeFormatter.format(inOneMonth.plusDays(1)));

        capacityInformationRepository.saveCapacityInformation(capacityInformation);

        capacityInformation = new CapacityInformation(serviceId0006, CapacityInformation.messageTemplate,
                dateTimeFormatter.format(inOneMonth.plusDays(2)));

        capacityInformationRepository.saveCapacityInformation(capacityInformation);


        capacityInformationRepository.deleteCapacityInformation(serviceId0006);

        capacityInformation = capacityInformationRepository.getCapacityInformationByServiceId(serviceId0006);
        assertNull(capacityInformation);

    }

    @Test
    public void testDeleteAll() {

        LocalDateTime inOneMonth = LocalDateTime.now().plusMonths(1);

        CapacityInformation capacityInformation;

        String serviceId0007 = "serviceId0007";

        capacityInformation = new CapacityInformation(serviceId0007, CapacityInformation.messageTemplate,
                dateTimeFormatter.format(inOneMonth));

        capacityInformationRepository.saveCapacityInformation(capacityInformation);

        capacityInformation = new CapacityInformation(serviceId0007, CapacityInformation.messageTemplate,
                dateTimeFormatter.format(inOneMonth.plusDays(1)));

        capacityInformationRepository.saveCapacityInformation(capacityInformation);

        capacityInformation = new CapacityInformation(serviceId0007, CapacityInformation.messageTemplate,
                dateTimeFormatter.format(inOneMonth.plusDays(2)));

        capacityInformationRepository.saveCapacityInformation(capacityInformation);

        String serviceId0008 = "serviceId0008";

        capacityInformation = new CapacityInformation(serviceId0008, CapacityInformation.messageTemplate,
                dateTimeFormatter.format(inOneMonth));

        capacityInformationRepository.saveCapacityInformation(capacityInformation);

        capacityInformation = new CapacityInformation(serviceId0008, CapacityInformation.messageTemplate,
                dateTimeFormatter.format(inOneMonth.plusDays(1)));

        capacityInformationRepository.saveCapacityInformation(capacityInformation);

        capacityInformation = new CapacityInformation(serviceId0008, CapacityInformation.messageTemplate,
                dateTimeFormatter.format(inOneMonth.plusDays(2)));

        capacityInformationRepository.saveCapacityInformation(capacityInformation);


        capacityInformationRepository.deleteAll();

        capacityInformation = capacityInformationRepository.getCapacityInformationByServiceId(serviceId0007);
        assertNull(capacityInformation);

        capacityInformation = capacityInformationRepository.getCapacityInformationByServiceId(serviceId0008);
        assertNull(capacityInformation);

    }

}
