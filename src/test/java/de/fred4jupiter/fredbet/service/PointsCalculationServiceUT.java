package de.fred4jupiter.fredbet.service;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@UnitTest
public class PointsCalculationServiceUT {

    @InjectMocks
    private PointsCalculationService pointsCalculationService;

    @Test
    public void threePointsOnExactResult() {
        Match match = createMatch(2, 1);
        Bet bet = createBet(2, 1);

        assertEquals(10, pointsCalculationService.calculatePointsFor(match, bet));
    }

    @Test
    public void twoPointsOnSameGoalDifference() {
        Match match = createMatch(2, 1);
        Bet bet = createBet(3, 2);

        assertEquals(3, pointsCalculationService.calculatePointsFor(match, bet));
    }

    @Test
    public void twoPointsOnSameGoalDifferenceHigherRange() {
        Match match = createMatch(10, 2);
        Bet bet = createBet(20, 12);

        assertEquals(3, pointsCalculationService.calculatePointsFor(match, bet));
    }

    @Test
    public void noPointsOnSameGoalDifferenceOtherIsWinner() {
        Match match = createMatch(4, 2);
        Bet bet = createBet(2, 4);

        assertEquals(0, pointsCalculationService.calculatePointsFor(match, bet));
    }

    @Test
    public void onePointOnWinner() {
        Match match = createMatch(6, 3);
        Bet bet = createBet(2, 1);

        assertEquals(3, pointsCalculationService.calculatePointsFor(match, bet));
    }

    @Test
    public void wrongResultNoPoints() {
        Match match = createMatch(6, 3);
        Bet bet = createBet(3, 4);

        assertEquals(0, pointsCalculationService.calculatePointsFor(match, bet));
    }

    @Test
    public void correctGoalDifferenceButWrongRelation() {
        Match match = createMatch(4, 3);
        Bet bet = createBet(3, 4);

        assertEquals(0, pointsCalculationService.calculatePointsFor(match, bet));
    }

    @Test
    public void extraPointOnCorrectPenaltyWinner() {
        Match match = MatchBuilder.create().withGroup(Group.ROUND_OF_SIXTEEN).withTeams("Deutschland", "Italien").withGoals(4, 4).build();
        match.setPenaltyWinnerOne(true);

        Bet bet = createBet(4, 4);
        bet.setPenaltyWinnerOne(true);

        assertEquals(11, pointsCalculationService.calculatePointsFor(match, bet));
    }

    @Test
    public void noPointsInFinalIfSetOfWin() {
        Match match = MatchBuilder.create().withGroup(Group.FINAL).withTeams("Deutschland", "Italien").withGoals(5, 5).build();
        match.setPenaltyWinnerOne(false);

        Bet bet = createBet(4, 3);
        bet.setPenaltyWinnerOne(true);

        assertEquals(0, pointsCalculationService.calculatePointsFor(match, bet));
    }

    @Test
    public void extraPointOnCorrectPenaltyOtherWinner() {
        Match match = MatchBuilder.create().withGroup(Group.ROUND_OF_SIXTEEN).withTeams("Deutschland", "Italien").withGoals(0, 0).build();
        match.setPenaltyWinnerOne(false);
        Bet bet = createBet(0, 0);
        bet.setPenaltyWinnerOne(false);

        assertEquals(11, pointsCalculationService.calculatePointsFor(match, bet));
    }

    @Test
    public void pointsWithJokerDoublesPoints() {
        Match match = MatchBuilder.create().withGroup(Group.GROUP_B).withTeams("Deutschland", "Italien").withGoals(0, 0).build();
        Bet bet = createBet(0, 0);
        bet.setPenaltyWinnerOne(false);
        bet.setJoker(true);

        assertEquals(20, pointsCalculationService.calculatePointsFor(match, bet));
    }

    @Test
    public void pointsWithJokerInPenaltyDoublesPoints() {
        Match match = MatchBuilder.create().withGroup(Group.ROUND_OF_SIXTEEN).withTeams("Deutschland", "Italien").withGoals(1, 1).build();
        match.setPenaltyWinnerOne(false);
        Bet bet = createBet(1, 1);
        bet.setPenaltyWinnerOne(false);
        bet.setJoker(true);

        assertEquals(22, pointsCalculationService.calculatePointsFor(match, bet));
    }

    @Test
    public void finalMatchCorrectBetWinnerOne() {
        Match match = createKnockoutMatch(2, 1, false);
        Bet bet = createKnockoutBet(2, 1, false);
        assertThat(pointsCalculationService.calculatePointsFor(match, bet)).isEqualTo(10);
    }

    @Test
    public void finalMatchCorrectBetWinnerTwo() {
        Match match = createKnockoutMatch(1, 2, false);
        Bet bet = createKnockoutBet(1, 2, false);
        assertThat(pointsCalculationService.calculatePointsFor(match, bet)).isEqualTo(10);
    }

    @Test
    public void finalMatchCorrectBetUndecidedPenaltyOne() {
        Match match = createKnockoutMatch(1, 1, true);
        Bet bet = createKnockoutBet(1, 1, true);
        assertThat(pointsCalculationService.calculatePointsFor(match, bet)).isEqualTo(11);
    }

    @Test
    public void finalMatchCorrectBetUndecidedPenaltyTwo() {
        Match match = createKnockoutMatch(1, 1, false);
        Bet bet = createKnockoutBet(1, 1, false);
        assertThat(pointsCalculationService.calculatePointsFor(match, bet)).isEqualTo(11);
    }

    @Test
    public void finalMatchWrongBetUndecidedPenaltyOne() {
        Match match = createKnockoutMatch(1, 1, true);
        Bet bet = createKnockoutBet(2, 1, false);
        assertThat(pointsCalculationService.calculatePointsFor(match, bet)).isEqualTo(3);
    }

    @Test
    public void finalMatchWrongBetUndecidedPenaltyTwo() {
        Match match = createKnockoutMatch(1, 1, false);
        Bet bet = createKnockoutBet(2, 1, false);
        assertThat(pointsCalculationService.calculatePointsFor(match, bet)).isEqualTo(3);
    }

    private Bet createBet(Integer goalsTeamOne, Integer goalsTeamTwo) {
        Bet bet = new Bet();
        bet.setGoalsTeamOne(goalsTeamOne);
        bet.setGoalsTeamTwo(goalsTeamTwo);
        return bet;
    }

    private Match createMatch(Integer goalsTeamOne, Integer goalsTeamTwo) {
        return MatchBuilder.create().withGroup(Group.GROUP_A).withTeams("Deutschland", "Italien").withGoals(goalsTeamOne, goalsTeamTwo)
                .build();
    }

    private Match createKnockoutMatch(Integer goalsTeamOne, Integer goalsTeamTwo, boolean penaltyWinnerOne) {
        return MatchBuilder.create()
                .withGroup(Group.FINAL)
                .withTeams(Country.GERMANY, Country.ITALY)
                .withGoals(goalsTeamOne, goalsTeamTwo)
                .withPenaltyWinnerOne(penaltyWinnerOne)
                .build();
    }

    private Bet createKnockoutBet(Integer goalsTeamOne, Integer goalsTeamTwo, boolean penaltyWinnerOne) {
        Bet bet = new Bet();
        bet.setGoalsTeamOne(goalsTeamOne);
        bet.setGoalsTeamTwo(goalsTeamTwo);
        bet.setPenaltyWinnerOne(penaltyWinnerOne);
        return bet;
    }
}
