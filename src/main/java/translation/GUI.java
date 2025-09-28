package translation;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;


// TODO Task D: Update the GUI for the program to align with UI shown in the README example.
//            Currently, the program only uses the CanadaTranslator and the user has
//            to manually enter the language code they want to use for the translation.
//            See the examples package for some code snippets that may be useful when updating
//            the GUI.
// TODO A: implement JSON Translator Done
// TODO B: have the language panel do a combobox dropdown selection Done
// TODO C: have the combobox take in languagecode into a country name Done
// TODO D: repeat with country panel (but use JList instead of combobox) Done
// TODO E: Reorder the UI, add tick, remove submit button Done
public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Translator translator = new JSONTranslator();
            JPanel countryPanel = new JPanel();
            //JTextField countryField = new JTextField(10);
            //countryPanel.add(new JLabel("Country:"));
            //countryPanel.setLayout(new GridLayout(0, 2));
            //countryPanel.add(new JLabel("Country:"), 0);
            //countryPanel.add(countryField);
            String[] items = new String[translator.getCountryCodes().size()];
            JComboBox<String> countryComboBox = new JComboBox<>();
            CountryCodeConverter countryconverter = new CountryCodeConverter();
            int i = 0;
            for(String countryCode : translator.getCountryCodes()) {
                String countryName = countryconverter.fromCountryCode(countryCode);
                items[i++] = countryName;
            }

            // create the JList with the array of strings and set it to allow multiple
            // items to be selected at once.
            JList<String> list = new JList<>(items);
            list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            // place the JList in a scroll pane so that it is scrollable in the UI
            JScrollPane scrollPane = new JScrollPane(list);
            countryPanel.add(scrollPane);

            list.addListSelectionListener(new ListSelectionListener() {

                /**
                 * Called whenever the value of the selection changes.
                 *
                 * @param e the event that characterizes the change.
                 */
                @Override
                public void valueChanged(ListSelectionEvent e) {

                    int[] indices = list.getSelectedIndices();
                    String[] items = new String[indices.length];
                    for (int i = 0; i < indices.length; i++) {
                        items[i] = list.getModel().getElementAt(indices[i]);
                    }

                    //JOptionPane.showMessageDialog(null, "User selected:" +
                    //        System.lineSeparator() + Arrays.toString(items));

                }
            });


            JPanel languagePanel = new JPanel();
            languagePanel.add(new JLabel("Language:"));
            // create combobox, add country codes into it, and add it to our panel
            JComboBox<String> languageComboBox = new JComboBox<>();
            LanguageCodeConverter converter = new LanguageCodeConverter();
            for(String countryCode : translator.getLanguageCodes()) {
                String langName = converter.fromLanguageCode(countryCode);
                languageComboBox.addItem(langName);
            }

            languageComboBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list,
                                                              Object value,
                                                              int index,
                                                              boolean isSelected,
                                                              boolean cellHasFocus) {
                    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                    if (index >= 0) { // dropdown list rows
                        Object selected = languageComboBox.getSelectedItem();
                        if (selected != null && selected.equals(value)) {
                            label.setText("✔ " + value.toString()); // tick for the chosen one
                        } else {
                            label.setText(value.toString());        // no tick
                        }
                    } else {
                        // Closed combo box (selected field): just show plain text
                        label.setText(value.toString());
                    }

                    return label;
                }
            });

            languagePanel.add(languageComboBox);

            // add listener for when an item is selected.
            languageComboBox.addItemListener(new ItemListener() {

                /**
                 * Invoked when an item has been selected or deselected by the user.
                 * The code written for this method performs the operations
                 * that need to occur when an item is selected (or deselected).
                 *
                 * @param e the event to be processed
                 */
                @Override
                public void itemStateChanged(ItemEvent e) {

                    if (e.getStateChange() == ItemEvent.SELECTED) {
                       String language = languageComboBox.getSelectedItem().toString();
                       //JOptionPane.showMessageDialog(null, "user selected " + language + "!");
                    }
                }


            });

            JPanel buttonPanel = new JPanel();

            JLabel resultLabelText = new JLabel("Translation:");
            buttonPanel.add(resultLabelText);
            JLabel resultLabel = new JLabel("\t\t\t\t\t\t\t");
            buttonPanel.add(resultLabel);


            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // simpler: one country

            list.addListSelectionListener(e -> {
                if (e.getValueIsAdjusting()) return; // fire once, not twice

                String languageName = (String) languageComboBox.getSelectedItem();
                String countryName  = list.getSelectedValue();

                if (languageName == null || countryName == null) {
                    resultLabel.setText("Please choose a language and a country.");
                    return;
                }

                String languageCode = converter.fromLanguage(languageName);     // e.g. "fr"
                String countryCode  = countryconverter.fromCountry(countryName); // e.g. "can"

                if (languageCode == null || countryCode == null) {
                    resultLabel.setText("Unsupported selection (no code mapping).");
                    return;
                }

                // normalize if your JSON expects lowercase
                languageCode = languageCode.toLowerCase();
                countryCode  = countryCode.toLowerCase();

                String result = translator.translate(countryCode, languageCode);
                resultLabel.setText(result != null ? result : "no translation found!");
            });

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(languagePanel);
            mainPanel.add(buttonPanel);
            mainPanel.add(countryPanel);


            JFrame frame = new JFrame("Country Name Translator");
            frame.setContentPane(mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);


        });
    }
}
