package cz.incad.kramerius.rights.server;

import org.aplikator.client.data.ListItem;
import org.aplikator.server.descriptor.Application;
import org.aplikator.server.descriptor.Collection;
import org.aplikator.server.descriptor.Entity;
import org.aplikator.server.descriptor.Property;
import org.aplikator.server.descriptor.Reference;

/**
 * Struktura databaze
 *
 * @author pavels
 */
public class Structure extends Application {

    /**
     * Entita uzivatel
     *
     * @author pavels
     */
    public class UserEntity extends Entity {
        // vlastnosti uzivatele
        public final Property<String> NAME;
        public final Property<String> SURNAME;
        public final Property<String> LOGINNAME;
        public final Property<String> PASSWORD;

        public final Property<String> EMAIL;
        public final Property<String> ORGANISATION;

        // administrator uzivatele
        public Reference<GroupEntity> PERSONAL_ADMIN;

        public Collection<GroupUserAssoction> GROUP_ASSOCIATIONS;

        public UserEntity() {
            super("Users_table", "USER_ENTITY", "USER_ID");
            NAME = stringProperty("NAME",  255, true);
            SURNAME = stringProperty("SURNAME",  255, true);
            LOGINNAME = stringProperty("LOGINNAME",  255, true);
            PASSWORD = stringProperty("PSWD",  255, false);

            EMAIL = stringProperty("EMAIL",  255, false);
            ORGANISATION = stringProperty("ORGANISATION",  255, false);

            addIndex("UNAME_IDX", false, NAME);
            addIndex("SURNAME_IDX", false, SURNAME);
            addIndex("LOGINNAME_IDX", false, LOGINNAME);
            addIndex("PASSWORD_IDX", false, PASSWORD);
        }
    }

    /**
     * Skupina
     *
     * @author pavels
     */
    public class GroupEntity extends Entity {

        // vlastnosti skupiny
        public final Property<String> GNAME;
        public final Property<String> DESCRIPTION;

        // admin skupiny
        public Reference<GroupEntity> PERSONAL_ADMIN;

        public Collection<GroupUserAssoction> USER_ASSOCIATIONS;

        public GroupEntity() {
            super("Groups_table", "GROUP_ENTITY", "GROUP_ID");
            GNAME = stringProperty("GNAME",  255, true);
            DESCRIPTION = stringProperty("DESCRIPTION",  1024, false);
            addIndex("GNAME_IDX", true, GNAME);
        }
    }

    public class GroupUserAssoction extends Entity {

        public final Reference<UserEntity> USERS;
        public final Reference<GroupEntity> GROUP;

        public GroupUserAssoction() {
            super("GroupTable_assoc", "GROUP_USER_ASSOC", "GROUP_USER_ASSOC_ID");
            USERS = referenceProperty(user, "USER_ID");
            GROUP = referenceProperty(group, "GROUP_ID");
        }
    }

    public class RightsEntity extends Entity {

        public final Property<String> UUID;
        public final Property<String> ACTION;
        public final Property<Integer> FIXED_PRIORITY;

        public Reference<RightCriteriumEntity> RIGHT_CRITERIUM;
        public Reference<UserEntity> USER;
        public Reference<GroupEntity> GROUP;

        public RightsEntity() {
            super("Rights_table", "RIGHT_ENTITY", "RIGHT_ID");
            UUID = stringProperty("UUID", 255, true);
            ACTION = stringProperty("ACTION", 255, true);

            RIGHT_CRITERIUM = referenceProperty(rightCriterium, "RIGHTS_CRIT");
            USER = referenceProperty(user, "USER_ID");
            GROUP = referenceProperty(group, "GROUP_ID");

            FIXED_PRIORITY = integerProperty("FIXED_PRIORITY");

            addIndex("UUID_IDX", false, UUID);
            addIndex("ACTION_IDX", false, ACTION);
        }
    }

    public class RightCriteriumEntity extends Entity {

        public final Property<String> QNAME;
        public final Property<Integer> TYPE;
        public Reference<RightCriteriumParamEntity> PARAM;

        @SuppressWarnings("unchecked")
        public RightCriteriumEntity() {
            super("Rights_criterium_table", "RIGHTS_CRITERIUM_ENTITY", "CRIT_ID");
            TYPE = integerProperty("TYPE");

            QNAME = stringProperty("QNAME", 255, true);
            QNAME.setListValues(new ListItem<String>("cz.incad.kramerius.security.impl.criteria.MovingWall", "cz.incad.kramerius.security.impl.criteria.MovingWall"), new ListItem<String>("cz.incad.kramerius.security.impl.criteria.DefaultIPAddressFilter", "cz.incad.kramerius.security.impl.criteria.DefaultIPAddressFilter"));

            PARAM = referenceProperty(criteriumParam, "citeriumParam");
        }
    }

    public class RightCriteriumParamEntity extends Entity {

        public final Property<String> VALS;
        public final Property<String> LONG_DESC;
        public final Property<String> SHORT_DESC;

        public RightCriteriumParamEntity() {
            super("Criterium_param_table", "CRITERIUM_PARAM_ENTITY", "CRIT_PARAM_ID");

            VALS = stringProperty("VALS",  1024, true);
            LONG_DESC = stringProperty("LONG_DESC",  1024, true);
            SHORT_DESC = stringProperty("SHORT_DESC",  256, true);
        }
    }

    public final UserEntity user = new UserEntity();
    public final GroupEntity group = new GroupEntity();
    public final GroupUserAssoction groupUserAssoction = new GroupUserAssoction();

    public final RightCriteriumParamEntity criteriumParam = new RightCriteriumParamEntity();
    public final RightCriteriumEntity rightCriterium = new RightCriteriumEntity();
    public final RightsEntity rights = new RightsEntity();

    public Structure() {

        group.USER_ASSOCIATIONS = group.reverseCollectionProperty("USERS", groupUserAssoction, groupUserAssoction.GROUP);
        user.GROUP_ASSOCIATIONS = user.reverseCollectionProperty("GROUPS", groupUserAssoction, groupUserAssoction.USERS);

        user.PERSONAL_ADMIN = user.referenceProperty(group, "PERSONAL_ADMIN_ID");
        group.PERSONAL_ADMIN = group.referenceProperty(group, "PERSONAL_ADMIN_ID");

        // digitalniReprezentace.ZVEREJNENO =
        // digitalniReprezentace.addReverseCollection("DIGITALNI_REPREZENTACE",
        // zverejneno, zverejneno.DIGITALNI_REPREZENTACE);

    }
}
