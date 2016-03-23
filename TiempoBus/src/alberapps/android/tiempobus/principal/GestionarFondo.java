/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p/>
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package alberapps.android.tiempobus.principal;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.data.backup.DatosBackup;

/**
 * Gestion de la imagen de fondo de la aplicacion
 */
public class GestionarFondo {

    /**
     * Cotexto principal
     */
    private MainActivity context;

    private SharedPreferences preferencias;

    public GestionarFondo(MainActivity contexto, SharedPreferences preferencia) {

        context = contexto;

        preferencias = preferencia;

    }

    /**
     * Decidir si galeria o fondo de color
     */
    public void seleccionarFondo() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Request missing location permission.
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MainActivity.REQUEST_CODE_STORAGE);
        } else {

            seleccionarFondoPermisos();

        }

    }

    public void seleccionarFondoPermisos() {

        final CharSequence[] items = {context.getResources().getString(R.string.seleccion_fondo_1), context.getResources().getString(R.string.seleccion_fondo_2),
                context.getResources().getString(R.string.seleccion_fondo_3)};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.preferencias_imagen);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                if (item == 0) {

                    Uri uri = getTempUri();

                    if (uri != null) {

                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        intent.putExtra("crop", "true");

                        intent.setType("image/*");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

                        context.startActivityForResult(intent, MainActivity.CARGAR_IMAGEN);

                    } else {
                        Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_fichero), Toast.LENGTH_SHORT).show();
                    }

                } else if (item == 1) {
                    View contenedor_principal = context.findViewById(R.id.contenedor_principal);
                    contenedor_principal.setBackgroundResource(R.color.background_material_light);

                    SharedPreferences.Editor editor = preferencias.edit();
                    editor.putString("image_galeria", "");
                    editor.commit();

                    Toast.makeText(context.getApplicationContext(), context.getResources().getText(R.string.seleccion_ok), Toast.LENGTH_SHORT).show();
                }

                if (UtilidadesUI.pantallaTabletHorizontal(context)) {
                    // Lanzar carga de vuelta
                    FragmentSecundarioTablet secundarioFrag = (FragmentSecundarioTablet) context.getSupportFragmentManager().findFragmentById(R.id.detalle_fragment);

                    if (secundarioFrag != null) {

                        secundarioFrag.setupFondoAplicacion();

                    }
                }

            }
        });

        AlertDialog alert = builder.create();

        alert.show();

    }


    private Uri getTempUri() {

        File file = getTempFile();

        if (file != null) {

            return Uri.fromFile(getTempFile());
        } else {
            return null;
        }
    }

    private File getTempFile() {
        if (isSDCARDMounted()) {

            File directorio = new File(Environment.getExternalStorageDirectory() + "/Android/data/alberapps.android.tiempobus/");
            directorio.mkdirs();

            File f = new File(Environment.getExternalStorageDirectory() + "/Android/data/alberapps.android.tiempobus/", "fotoFondo.jpg");
            try {
                f.createNewFile();
            } catch (IOException e) {

                // int i = 1;

            }
            return f;
        } else {
            return null;
        }
    }

    private boolean isSDCARDMounted() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * cambiar el fondo de pantalla con la galeria
     *
     * @param uri
     */
    public void activarNuevoFondo(Uri uri) {

        // Uri de la nueva imagen
        File tempFile = getTempFile();

        if (tempFile != null) {

            String filePath = tempFile.getPath();

            // Guardar
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putString("image_galeria", filePath);
            editor.commit();

            setupFondoAplicacion();

            Toast.makeText(context, context.getResources().getText(R.string.seleccion_ok), Toast.LENGTH_SHORT).show();

        }

    }


    /**
     * Activar fondo para nuevas versiones de la galeria
     *
     * @param uri
     */
    public void activarNuevoFondo19(Uri uri) {

        InputStream origen = null;

        File tempFile = null;

        FileOutputStream destino = null;

        try {

            // Uri de la nueva imagen
            tempFile = getTempFile();

            //Fichero seleccionado en la galeria
            origen = context.getContentResolver().openInputStream(uri);

            destino = new FileOutputStream(tempFile);

            //Copiar el archivo a la ruta de la aplicacion
            DatosBackup.copyFileI(origen, destino);


            //Guardar configuracion y activar nuevo fondo
            if (tempFile != null && uri != null) {

                String filePath = tempFile.getPath();

                // Guardar
                SharedPreferences.Editor editor = preferencias.edit();
                editor.putString("image_galeria", filePath);
                editor.commit();

                setupFondoAplicacion();

                Toast.makeText(context, context.getResources().getText(R.string.seleccion_ok), Toast.LENGTH_SHORT).show();

            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {

                if (origen != null) {
                    origen.close();
                }
                if (destino != null) {
                    destino.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }


    /**
     * Seleccion del fondo de la galeria en el arranque
     */
    public void setupFondoAplicacion() {

        try {

            String fondo_galeria = preferencias.getString("image_galeria", "");

            View contenedor_principal = context.findViewById(R.id.contenedor_principal);

            UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, context);

            if (UtilidadesUI.pantallaTabletHorizontal(context)) {
                // Lanzar carga de vuelta
                FragmentSecundarioTablet secundarioFrag = (FragmentSecundarioTablet) context.getSupportFragmentManager().findFragmentById(R.id.detalle_fragment);

                if (secundarioFrag != null) {

                    secundarioFrag.setupFondoAplicacion();

                }
            }

        } catch (Exception e) {

            SharedPreferences.Editor editor = preferencias.edit();
            editor.putString("image_galeria", "");
            editor.commit();

            View contenedor_principal = context.findViewById(R.id.contenedor_principal);

            String fondo_galeria = preferencias.getString("image_galeria", "");

            UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, context);

            if (UtilidadesUI.pantallaTabletHorizontal(context)) {
                // Lanzar carga de vuelta
                FragmentSecundarioTablet secundarioFrag = (FragmentSecundarioTablet) context.getSupportFragmentManager().findFragmentById(R.id.detalle_fragment);

                if (secundarioFrag != null) {

                    secundarioFrag.setupFondoAplicacion();

                }
            }

            Toast.makeText(context, context.getResources().getText(R.string.error_fondo), Toast.LENGTH_SHORT).show();

        }

    }

}
