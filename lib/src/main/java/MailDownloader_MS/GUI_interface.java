package MailDownloader_MS;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
//import javax.swing.JPanel;
//import java.awt.BorderLayout;
//import java.awt.FlowLayout;
//import java.awt.GridLayout;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.SystemColor;
import javax.swing.UIManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//import com.google.gson.JsonParseException;

import javax.swing.JTextField;
import javax.swing.JProgressBar;
//import javax.swing.JPopupMenu;
//import java.awt.Component;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GUI_interface {

	private JFrame frmMaildownloaderoutlookV;
	private JTextField input_path;
	private JTextField input_date_start;
	private JTextField input_date_end;
	//private ActionEvent actionEvent;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI_interface window = new GUI_interface();
					window.frmMaildownloaderoutlookV.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI_interface() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		JSONObject config = new JSONObject();
		JSONArray config_array = new JSONArray();
		JSONParser jsonParser = new JSONParser();
		
		String file_path = null;
		String fecha_inicio = null;
		String fecha_final = null;
		
		try 
        {
			FileReader reader = new FileReader("config.json");
            //Read JSON file
            Object obj = jsonParser.parse(reader);
 
            config_array = (JSONArray) obj;
            System.out.println(config_array);
            
            

            config = (JSONObject) config_array.get(0);
            file_path = (String) config.get("output_path");
            fecha_inicio = (String) config.get("fecha_inicial");
            fecha_final = (String) config.get("fecha_final");
 
        } catch (FileNotFoundException e) {
        	System.out.println("Error 1000");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
		
		frmMaildownloaderoutlookV = new JFrame();
		frmMaildownloaderoutlookV.getContentPane().setForeground(new Color(0, 0, 0));
		frmMaildownloaderoutlookV.getContentPane().setBackground(Color.LIGHT_GRAY);
		frmMaildownloaderoutlookV.setBackground(new Color(128, 128, 128));
		frmMaildownloaderoutlookV.setResizable(false);
		frmMaildownloaderoutlookV.setTitle("MailDownloader-Outlook V0.7");
		frmMaildownloaderoutlookV.setBounds(100, 100, 465, 600);
		frmMaildownloaderoutlookV.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMaildownloaderoutlookV.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Correo conectado: ");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(114, 91, 191, 41);
		frmMaildownloaderoutlookV.getContentPane().add(lblNewLabel);
		
		JLabel lb_actual_email = new JLabel("CorreoActual");
		lb_actual_email.setHorizontalAlignment(SwingConstants.CENTER);
		lb_actual_email.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lb_actual_email.setBounds(140, 131, 133, 14);
		frmMaildownloaderoutlookV.getContentPane().add(lb_actual_email);
		
		JButton btn_disconnect = new JButton("DESCONECTAR");
		JButton btn_connect = new JButton("CONECTAR");
		
		
		btn_connect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btn_connect.setEnabled(false);
				App.main(null);
				btn_disconnect.setEnabled(true);
				lb_actual_email.setText(Graph.getUser(App.getAccessTokenFromAuthService()).displayName);
			}
		});
		btn_disconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btn_disconnect.setEnabled(false);
				App.logOut();
				btn_connect.setEnabled(true);
			}
		});
		btn_connect.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_connect.setBackground(UIManager.getColor("Button.darkShadow"));
		btn_connect.setForeground(new Color(0, 0, 255));
		btn_connect.setBounds(51, 35, 150, 45);
		frmMaildownloaderoutlookV.getContentPane().add(btn_connect);
		
		btn_disconnect.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_disconnect.setForeground(new Color(255, 0, 0));
		btn_disconnect.setBackground(UIManager.getColor("Button.darkShadow"));
		btn_disconnect.setBounds(233, 35, 150, 45);
		frmMaildownloaderoutlookV.getContentPane().add(btn_disconnect);
		
		JLabel lblNewLabel_2 = new JLabel("Directorio de salida:");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBounds(133, 169, 150, 29);
		frmMaildownloaderoutlookV.getContentPane().add(lblNewLabel_2);
		
		input_path = new JTextField(file_path);
		input_path.setBounds(51, 209, 332, 20);
		frmMaildownloaderoutlookV.getContentPane().add(input_path);
		input_path.setColumns(10);
		
		JLabel lblNewLabel_2_1 = new JLabel("Fecha de inicio(formato: yyyy/mm/dd):");
		lblNewLabel_2_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_2_1.setBounds(88, 240, 275, 29);
		frmMaildownloaderoutlookV.getContentPane().add(lblNewLabel_2_1);
		
		input_date_start = new JTextField(fecha_inicio);
		input_date_start.setColumns(10);
		input_date_start.setBounds(51, 271, 332, 20);
		frmMaildownloaderoutlookV.getContentPane().add(input_date_start);
		
		JLabel lblNewLabel_2_1_1 = new JLabel("Fecha final(formato: yyyy/mm/dd):");
		lblNewLabel_2_1_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2_1_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_2_1_1.setBounds(99, 302, 253, 29);
		frmMaildownloaderoutlookV.getContentPane().add(lblNewLabel_2_1_1);
		
		input_date_end = new JTextField(fecha_final);
		input_date_end.setColumns(10);
		input_date_end.setBounds(51, 342, 332, 20);
		frmMaildownloaderoutlookV.getContentPane().add(input_date_end);
		
		JButton tbn_update = new JButton("Actualizar Configuracion");
		tbn_update.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				JSONObject config = new JSONObject();
				JSONArray config_array = new JSONArray();
				File file_out =new File("config.json");
				
				config.put("output_path",input_path.getText());
		        config.put("fecha_inicial",input_date_start.getText());
		        config.put("fecha_final",input_date_end.getText());
		        config_array.add(config);
		        
		        FileWriter myWriter;
				try {	
						file_out.delete();
					    file_out.createNewFile();
						myWriter = new FileWriter("config.json");
						myWriter.write(config_array.toJSONString());
					    myWriter.flush();
					    myWriter.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			  
				
			}
		});
		tbn_update.setForeground(SystemColor.textHighlight);
		tbn_update.setFont(new Font("Tahoma", Font.PLAIN, 14));
		tbn_update.setBounds(114, 373, 191, 41);
		frmMaildownloaderoutlookV.getContentPane().add(tbn_update);
		
		JButton btn_download = new JButton("Descargar");
		btn_download.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] arg = {"action"};
				App.main(arg);
			}
		});
		btn_download.setForeground(new Color(50, 205, 50));
		btn_download.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_download.setBounds(140, 505, 133, 45);
		frmMaildownloaderoutlookV.getContentPane().add(btn_download);
		
		JLabel lblNewLabel_3 = new JLabel("ESTADO:");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_3.setBounds(163, 425, 79, 20);
		frmMaildownloaderoutlookV.getContentPane().add(lblNewLabel_3);
		
		JLabel lb_state = new JLabel("Estado");
		lb_state.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lb_state.setHorizontalAlignment(SwingConstants.CENTER);
		lb_state.setBounds(163, 450, 87, 20);
		frmMaildownloaderoutlookV.getContentPane().add(lb_state);
		
		JProgressBar pb_state = new JProgressBar();
		pb_state.setBounds(51, 481, 332, 20);
		frmMaildownloaderoutlookV.getContentPane().add(pb_state);
		
		JButton btnNewButton = new JButton("?");
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnNewButton.setForeground(new Color(255, 0, 0));
		btnNewButton.setBounds(393, 199, 41, 41);
		frmMaildownloaderoutlookV.getContentPane().add(btnNewButton);
	}
}
