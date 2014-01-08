/**
 * 
 */
package de.hybris.platform.cuppytrail.facades.impl;

import de.hybris.platform.cuppy.model.MatchModel;
import de.hybris.platform.cuppytrail.StadiumService;
import de.hybris.platform.cuppytrail.data.MatchSummaryData;
import de.hybris.platform.cuppytrail.data.StadiumData;
import de.hybris.platform.cuppytrail.facades.StadiumFacade;
import de.hybris.platform.cuppytrail.model.StadiumModel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * @author radmike
 * 
 */
public class DefaultStadiumFacade implements StadiumFacade
{

	private StadiumService stadiumService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.cuppytrail.facades.StadiumFacade#getStadium(java.lang.String)
	 */
	@Override
	public StadiumData getStadium(final String name)
	{
		final StadiumModel stadium = stadiumService.getStadiumForCode(name);

		if (stadium == null)
		{
			return null;
		}

		// Create a list of MatchSummaryData from the matches
		final List<MatchSummaryData> matchSummary = new ArrayList<MatchSummaryData>();
		if (stadium.getMatches() != null)
		{
			final Iterator<MatchModel> matchesIterator = stadium.getMatches().iterator();

			while (matchesIterator.hasNext())
			{
				final MatchModel match = matchesIterator.next();
				final MatchSummaryData summary = new MatchSummaryData();
				final String homeTeam = match.getHomeTeam().getName();
				final String guestTeam = match.getGuestTeam().getName();
				final Date matchDate = match.getDate();
				// If no goals are specified provide a user friendly "-" instead of null
				final String guestGoals = match.getGuestGoals() == null ? "-" : match.getGuestGoals().toString();
				final String homeGoals = match.getHomeGoals() == null ? "-" : match.getHomeGoals().toString();
				summary.setHomeTeam(homeTeam);
				summary.setGuestTeam(guestTeam);
				summary.setDate(matchDate);
				summary.setGuestGoals(guestGoals);
				summary.setHomeGoals(homeGoals);
				final String formatedDate = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(matchDate);
				final String matchSummaryFormatted = homeTeam + ":( " + homeGoals + " ) " + guestTeam + " ( " + guestGoals + " ) "
						+ formatedDate;
				summary.setMatchSummaryFormatted(matchSummaryFormatted);
				matchSummary.add(summary);
			}
		}

		final StadiumData stadiumData = new StadiumData();
		stadiumData.setCapacity(stadium.getCapacity().toString());
		stadiumData.setName(stadium.getCode());
		stadiumData.setMatches(matchSummary);
		stadiumData.setType(stadium.getStadiumType().getCode());
		stadiumData.setAccess(stadium.getStadiumAccess().getCode());

		return stadiumData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.cuppytrail.facades.StadiumFacade#getStadiums()
	 */
	@Override
	public List<StadiumData> getStadiums()
	{
		final List<StadiumModel> stadiumModels = stadiumService.getStadiums();
		final List<StadiumData> stadiumFacadeData = new ArrayList<StadiumData>();

		for (final StadiumModel stadiumModel : stadiumModels)
		{
			final StadiumData stadiumData = new StadiumData();
			stadiumData.setName(stadiumModel.getCode());
			stadiumData.setCapacity(stadiumModel.getCapacity().toString());
			stadiumData.setType(stadiumModel.getStadiumType().getCode());
			stadiumData.setAccess(stadiumModel.getStadiumAccess().getCode());
			stadiumFacadeData.add(stadiumData);
		}

		return stadiumFacadeData;
	}

	@Required
	public void setStadiumService(final StadiumService stadiumService)
	{
		this.stadiumService = stadiumService;
	}
}
