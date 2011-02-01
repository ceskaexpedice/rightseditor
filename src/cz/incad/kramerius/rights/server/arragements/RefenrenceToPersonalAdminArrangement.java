package cz.incad.kramerius.rights.server.arragements;

import org.aplikator.server.descriptor.Arrangement;
import org.aplikator.server.descriptor.Form;
import org.aplikator.server.descriptor.HorizontalPanel;
import org.aplikator.server.descriptor.QueryGenerator;
import org.aplikator.server.descriptor.RefButton;
import org.aplikator.server.descriptor.TextArea;
import org.aplikator.server.descriptor.TextField;
import org.aplikator.server.descriptor.VerticalPanel;

import cz.incad.kramerius.rights.server.Structure;
import cz.incad.kramerius.rights.server.Structure.GroupEntity;

public class RefenrenceToPersonalAdminArrangement extends Arrangement {

	Structure struct;
	
	public RefenrenceToPersonalAdminArrangement(Structure structure) {
		super(structure.group);
		this.struct = structure;
		addProperty(struct.group.GNAME);
		setSortProperty(struct.group.GNAME);
		setForm(createGroupForm());
	}
	
	
	private Form createGroupForm() {
		Form form = new Form();
		form.setLayout(new VerticalPanel().addChild(
				new VerticalPanel()
					.addChild(new TextField(struct.group.GNAME))
					.addChild(new TextArea(struct.group.DESCRIPTION))

		));
		return form;
	}
	

}
