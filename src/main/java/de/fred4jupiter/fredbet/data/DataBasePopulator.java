package de.fred4jupiter.fredbet.data;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.FredBetRole;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.MatchBuilder;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.service.UserService;

@Component
public class DataBasePopulator {

    private static final Logger LOG = LoggerFactory.getLogger(DataBasePopulator.class);

    @Autowired
    private MatchService matchService;

    @Autowired
    private Environment environment;

    @Autowired
    private UserService userService;

    @Autowired
    private BettingService bettingService;

    @PostConstruct
    public void initDatabaseWithDemoData() {
        LOG.info("initDatabaseWithDemoData: inserting demo data...");

        // will also be used for remote shell login
        final AppUser adminUser = new AppUser("michael", "michael", FredBetRole.ROLE_USER, FredBetRole.ROLE_ADMIN, FredBetRole.ROLE_EDIT_MATCH);
        userService.save(adminUser);
        final AppUser editUser = new AppUser("edit", "edit", FredBetRole.ROLE_USER, FredBetRole.ROLE_EDIT_MATCH);
        userService.save(editUser);
        
        final AppUser normalUser = new AppUser("normal", "normal", FredBetRole.ROLE_USER);
        userService.save(normalUser);

        // this we be executed in demodata profile only
        if (environment.acceptsProfiles("demodata")) {

            // for (int i = 0; i < 10; i++) {
            // userService.save(new AppUser("test" + i, "test" + i,
            // FredBetRole.ROLE_USER));
            // }

            Match match1 = MatchBuilder.create().withTeams("Bulgarien", "Irland").withGroup(Group.GROUP_A)
                    .withStadium("Westfalenstadium, Dortmund").withKickOffDate(LocalDateTime.now().plusMinutes(5)).build();
            matchService.save(match1);

            Match match2 = MatchBuilder.create().withTeams("Deutschland", "Frankfreich").withGroup(Group.GROUP_B)
                    .withStadium("Weserstadium, bremen").withKickOffDate(LocalDateTime.now().plusMinutes(10)).build();
            matchService.save(match2);

            Match match3 = MatchBuilder.create().withTeams("Belgien", "England").withGroup(Group.GROUP_D).withStadium("AOL Arena, München")
                    .withKickOffDate(LocalDateTime.now().plusMinutes(15)).build();
            matchService.save(match3);

            bettingService.createAndSaveBetting(adminUser, match1, 2, 1);
            bettingService.createAndSaveBetting(adminUser, match2, 3, 1);
            bettingService.createAndSaveBetting(adminUser, match3, 2, 4);

            bettingService.createAndSaveBetting(editUser, match1, 0, 1);
            bettingService.createAndSaveBetting(editUser, match2, 1, 2);
            bettingService.createAndSaveBetting(editUser, match3, 5, 0);
        }
    }

}
