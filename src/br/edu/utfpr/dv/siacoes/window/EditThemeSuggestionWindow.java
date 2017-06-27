package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.ThemeSuggestionBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.ThemeSuggestion;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditThemeSuggestionWindow extends EditWindow {

	private final ThemeSuggestion theme;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final TextField textTitle;
	private final TextField textProponent;
	private final TextArea textObjetives;
	private final TextArea textProposal;
	private final CheckBox checkActive;
	private final DateField textSubmissionDate;
	
	public EditThemeSuggestionWindow(ThemeSuggestion theme, ListView parentView){
		super("Editar Sugest�o", parentView);
		
		if(theme == null){
			this.theme = new ThemeSuggestion();
			this.theme.setDepartment(Session.getUser().getDepartment());
			this.theme.setUser(Session.getUser());
		}else{
			this.theme = theme;
		}
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setEnabled(false);
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		
		this.textTitle = new TextField("T�tulo");
		this.textTitle.setWidth("800px");
		this.textTitle.setMaxLength(255);
		
		this.textProponent = new TextField("Proponente");
		this.textProponent.setWidth("800px");
		this.textProponent.setMaxLength(255);
		
		this.textObjetives = new TextArea("Objetivos");
		this.textObjetives.setWidth("800px");
		
		this.textProposal = new TextArea("Descri��o da Proposta (Caracteriza��o do problema e justificativa)");
		this.textProposal.setWidth("800px");
		
		this.checkActive = new CheckBox("Ativo");
		
		this.textSubmissionDate = new DateField("Data de Submiss�o");
		this.textSubmissionDate.setDateFormat("dd/MM/yyyy");
		this.textSubmissionDate.setEnabled(false);
		
		this.addField(new HorizontalLayout(this.comboCampus, this.comboDepartment));
		this.addField(this.textTitle);
		this.addField(this.textProponent);
		this.addField(this.textProposal);
		this.addField(this.textObjetives);
		this.addField(new HorizontalLayout(this.textSubmissionDate, this.checkActive));
		
		if(!Session.isUserProfessor() || (!Session.isUserManager(SystemModule.SIGET) && (Session.getUser().getIdUser() != this.theme.getUser().getIdUser()))){
			this.checkActive.setVisible(false);
			this.setSaveButtonEnabled(false);
			this.setCaption("Visualizar Sugest�o");
		}
		
		this.loadTheme();
	}
	
	private void loadTheme(){
		try{
			CampusBO bo = new CampusBO();
			Campus campus = bo.findByDepartment(this.theme.getDepartment().getIdDepartment());
			
			this.comboCampus.setCampus(campus);
			
			this.comboDepartment.setIdCampus(campus.getIdCampus());
			
			this.comboDepartment.setDepartment(this.theme.getDepartment());
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.textTitle.setValue(this.theme.getTitle());
		this.textProponent.setValue(this.theme.getProponent());
		this.textObjetives.setValue(this.theme.getObjectives());
		this.textProposal.setValue(this.theme.getProposal());
		this.textSubmissionDate.setValue(this.theme.getSubmissionDate());
		this.checkActive.setValue(this.theme.isActive());
	}
	
	@Override
	public void save() {
		try {
			ThemeSuggestionBO bo = new ThemeSuggestionBO();
			
			this.theme.setTitle(this.textTitle.getValue());
			this.theme.setProponent(this.textProponent.getValue());
			this.theme.setProposal(this.textProposal.getValue());
			this.theme.setObjectives(this.textObjetives.getValue());
			this.theme.setSubmissionDate(this.textSubmissionDate.getValue());
			this.theme.setActive(this.checkActive.getValue());
			
			bo.save(this.theme);
			
			Notification.show("Salvar Sugest�o", "Sugest�o salva com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.parentViewRefreshGrid();
			this.close();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Sugest�o", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

}