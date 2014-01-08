/**
 * 
 */
package de.hybris.platform.cuppytrail.interceptors;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.platform.cuppy.model.NewsModel;
import de.hybris.platform.cuppytrail.model.StadiumModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author radmike
 * 
 */
public class StadiumCapacityIntegrationTest extends ServicelayerTest
{
	@Resource
	private ModelService modelService;
	@Resource
	private FlexibleSearchService flexibleSearchService;

	@SuppressWarnings("unused")
	static final private Logger LOG = Logger.getLogger(StadiumCapacityIntegrationTest.class);

	@BeforeClass
	public static void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
	}

	@Test
	public void testValidationInterceptor()
	{
		//given
		final StadiumModel stadium = modelService.create(StadiumModel.class);
		stadium.setCode("invalid");
		stadium.setCapacity(Integer.valueOf(200000));

		//when
		try
		{
			modelService.save(stadium);
			Assert.fail();
		}
		//then
		catch (final ModelSavingException e)
		{
			assertThat(e.getCause().getClass()).isEqualTo(InterceptorException.class);
			assertThat(e.getMessage()).contains("Capactity is too high");
		}
	}

	@Test
	public void testEventSending() throws Exception
	{
		//given
		final StadiumModel stadium = modelService.create(StadiumModel.class);
		final Random rnd = new Random();
		final String code = "stadium(" + System.currentTimeMillis() + "|" + rnd.nextInt() + ")";
		stadium.setCode(code);
		stadium.setCapacity(Integer.valueOf(80000));

		//when
		modelService.save(stadium);

		//then
		assertThat(findLastNews()).isNull();
		Thread.sleep(4000);
		assertThat(findLastNews().getContent(Locale.ENGLISH)).contains(code);
	}

	private NewsModel findLastNews()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("SELECT {n:").append(NewsModel.PK).append("} ");
		builder.append("FROM {").append(NewsModel._TYPECODE).append(" AS n} ");
		builder.append("WHERE ").append("{n:").append(NewsModel.COMPETITION).append("} IS NULL ");
		builder.append("ORDER BY ").append("{n:").append(NewsModel.CREATIONTIME).append("} DESC");

		final List<NewsModel> list = flexibleSearchService.<NewsModel> search(builder.toString()).getResult();
		if (list.isEmpty())
		{
			return null;
		}
		else
		{
			return list.get(0);
		}
	}
}
