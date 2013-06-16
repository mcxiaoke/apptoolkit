package com.mcxiaoke.shell.others;

import android.app.ListActivity;

public class FileBrowser extends ListActivity {

/*	private enum DISPLAYMODE {
		ABSOLUTE, RELATIVE;
	}

	private final DISPLAYMODE displayMode = DISPLAYMODE.RELATIVE;
	private List<IconifiedText> directoryEntries = new ArrayList<IconifiedText>();
	private List<File> flashList = new ArrayList<File>();
	private File currentDirectory = new File("/");
	private File copyFile = new File("");
	private File previousDirectory = new File("/");
	private boolean moveSelect = false;

	*//** Called when the activity is first created. *//*
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		browseToRoot();
		startUpMount();
	}

	*//**
	 * This function browses to the root-directory of the file-system.
	 *//*
	private void browseToRoot() {
		browseTo(new File("/"));
	}

	*//**
	 * This function browses to the sdcard-directory of the file-system.
	 *//*
	private void browseToSdcard() {
		browseTo(new File("/mnt/sdcard/"));
	}

	*//**
	 * This function mounts the file system as read only to be able to view all
	 * the directories on the phone
	 *//*
	private void startUpMount() {
		if (ShellInterface.isSuAvailable()) {
			ShellInterface.runCommand("mount -o remount,rw /");
			ShellInterface.runCommand("chmod a+r *//*");
			ShellInterface.runCommand("chmod a+r /cache*//*");
			ShellInterface.runCommand("chmod a+r /config*//*");
			ShellInterface.runCommand("chmod a+r /sbin*//*");
			ShellInterface.runCommand("chmod a+r /root*//*");
			ShellInterface.runCommand("chmod a+r /data/dalvik-cache");
			ShellInterface.runCommand("chmod a+rw /mnt/sdcard");
			ShellInterface.runCommand("mount -o remount,ro /system");
			ShellInterface.runCommand("mount -o remount,rw /data");
			ShellInterface.runCommand("mount -o remount,ro /");
		}
	}

	*//**
	 * This function browses up one level according to the field:
	 * currentDirectory
	 *//*
	private void upOneLevel() {
		if (this.currentDirectory.getParent() != null) {
			this.browseTo(this.currentDirectory.getParentFile());
		}
	}

	public boolean browseTo(final File aDirectory) {
		// On relative we display the full path in the title.
		if (this.displayMode == DISPLAYMODE.RELATIVE)
			this.setTitle(aDirectory.getAbsolutePath() + " :: "
					+ getString(R.string.app_name));

		// Check to see if the current directory is a file or not
		if (aDirectory.isDirectory()) {
			this.currentDirectory = aDirectory;
			// If its a file, fill the directory
			fill(aDirectory.listFiles());
			registerForContextMenu(getListView());

			return true;
		} else {
			registerForContextMenu(getListView());
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Open " + aDirectory.getName() + "?")
					.setCancelable(false)
					.setIcon(R.drawable.info)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									FileBrowser.this.openFile(aDirectory);
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();

								}
							});
			AlertDialog alert = builder.create();
			alert.show();
			return false;
		}
	}

	*//**
	 * This function creates a new intent to open a file with a certain MIME
	 * type.
	 *//*
	private void openFile(File aFile) {
		Intent myIntent = new Intent(Intent.ACTION_VIEW);
		File file = new File(aFile.getAbsolutePath());
		String type = getMIMEType(aFile);
		myIntent.setDataAndType(Uri.fromFile(file), type);
		startActivity(myIntent);
	}

	*//**
	 * This function uses a file's extention to dermine the correct MIME type.
	 *//*
	private String getMIMEType(File f)

	{

		String end = f
				.getName()
				.substring(f.getName().lastIndexOf(".") + 1,
						f.getName().length()).toLowerCase();

		String type = "";

		if (end.equals("mp3") || end.equals("aac") || end.equals("aac")
				|| end.equals("amr") || end.equals("mpeg") || end.equals("mp4"))
			type = "audio*//*";
		else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg"))
			type = "image*//*";
		else if (end.equals("zip") || end.equals("rar") || end.equals("7z"))
			type = "application*//*";
		else if (end.equals("apk"))
			type = "application/vnd.android.package-archive";
		else if (end.equals("txt") || end.equals("prop") || end.equals("log")
				|| end.equals("rc") || end.equals("xml"))
			type = "text*//*";
		else
			type = "text*//*";

		return type;

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		String selectedFileString = this.directoryEntries.getIcon(position)
				.getText();
		if (selectedFileString.equals(getString(R.string.current_dir))) {
			// Refresh
			this.browseTo(this.currentDirectory);
		} else if (selectedFileString.equals(getString(R.string.up_one_level))) {
			this.upOneLevel();
		} else {
			File clickedFile = null;
			switch (this.displayMode) {
			case RELATIVE:
				clickedFile = new File(this.currentDirectory.getAbsolutePath()
						+ this.directoryEntries.getIcon(position).getText());
				break;
			case ABSOLUTE:
				clickedFile = new File(this.directoryEntries.getIcon(position)
						.getText());
				break;
			}
			if (clickedFile != null)
				this.browseTo(clickedFile);
		}
	}

	private void fill(File[] files) {
		this.directoryEntries.clear();

		Comparator<File> byDirThenAlpha = new DirAlphaComparator();
		Arrays.sort(files, byDirThenAlpha);

		// Add the "." == "current directory"
		*//*
		 * this.directoryEntries.add(new IconifiedText(
		 * getString(R.string.current_dir), getResources().getDrawable(
		 * R.drawable.folder)));
		 *//*
		// and the ".." == 'Up one level'
		if (this.currentDirectory.getParent() != null)
			this.directoryEntries.add(new IconifiedText(
					getString(R.string.up_one_level), getResources()
							.getDrawable(R.drawable.folder), ""));

		Drawable currentIcon = null;
		for (File currentFile : files) {
			if (currentFile.isDirectory()) {
				currentIcon = getResources().getDrawable(R.drawable.folder);
			} else {
				String fileName = currentFile.getName();
				*//*
				 * Determine the Icon to be used, depending on the FileEndings
				 * defined in: res/values/fileendings.xml.
				 *//*
				if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingImage))) {
					currentIcon = getResources().getDrawable(R.drawable.image);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingWebText))) {
					currentIcon = getResources()
							.getDrawable(R.drawable.webtext);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingPackage))) {
					currentIcon = getResources().getDrawable(R.drawable.packed);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingAudio))) {
					currentIcon = getResources().getDrawable(R.drawable.audio);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingVideo))) {
					currentIcon = getResources().getDrawable(R.drawable.video);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingDB))) {
					currentIcon = getResources().getDrawable(
							R.drawable.database);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingFlash))) {
					currentIcon = getResources().getDrawable(R.drawable.flash);
				} else {
					currentIcon = getResources().getDrawable(R.drawable.text);
				}
			}

			switch (this.displayMode) {
			case ABSOLUTE:
				*//* On absolute Mode, we show the full path *//*
				this.directoryEntries.add(new IconifiedText(currentFile
						.getPath(), currentIcon, ""));
				break;
			case RELATIVE:
				*//*
				 * On relative Mode, we have to cut the current-path at the
				 * beginning
				 *//*
				int currentPathStringLenght = this.currentDirectory
						.getAbsolutePath().length();

				this.directoryEntries.add(new IconifiedText(currentFile
						.getAbsolutePath().substring(currentPathStringLenght),
						currentIcon, ""));

				break;
			}

		}
		// Comparator<File> byDirThenAlpha = new DirAlphaComparator();

		// Collections.sort(this.directoryEntries, byDirThenAlpha);
		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this);
		itla.setListItems(this.directoryEntries);
		this.setListAdapter(itla);
	}

	class DirAlphaComparator implements Comparator<File> {

		// Comparator interface requires defining compare method.
		public int compare(File filea, File fileb) {
			// ... Sort directories before files,
			// otherwise alphabetical ignoring case.
			if (filea.isDirectory() && !fileb.isDirectory()) {
				return -1;

			} else if (!filea.isDirectory() && fileb.isDirectory()) {
				return 1;

			} else {
				return filea.getName().compareToIgnoreCase(fileb.getName());
			}
		}
	}

	public String getFileInfo(File currentFile) {

		String info = new String();

		// File Name
		String fileName = currentFile.getName();
		// Get Timestamp
		final long timeStamp = currentFile.lastModified();
		Date d = new Date(timeStamp);
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
		String dateString = sdf.format(d);

		// Get Size of File or Dir
		long fileSize;
		if (currentFile.isDirectory()) {
			fileSize = 0;
		} else {
			fileSize = currentFile.length();
		}
		// long fileSize = getFolderSize(propertiesFile.listFiles());
		String fileSizeString = convertSizetoHuman(fileSize);

		// Get file or dir permissions
		String filePermissionsFinal = getFilePermissions(currentFile);

		// Get file or dir owner
		String fileOwner = getFileOwner(currentFile);

		// getIcon file or dir group
		String fileGroup = getFileGroup(currentFile);

		if (currentFile.isDirectory()) {

			info = "\n" + dateString + " " + filePermissionsFinal + " ";
		}

		else {
			info = "\n" + dateString + " " + filePermissionsFinal + " "
					+ fileSizeString;
		}

		*//*
		 * info = "Name: " + fileName + "\n" + "Size: " + fileSizeString + "\n"
		 * + "Timestamp: " + dateString + "\n" + "Permissions: " +
		 * filePermissionsFinal + "\n" + "Owner: " + fileOwner + "\n" +
		 * "Group: " + fileGroup;
		 *//*
		return info;
	}

	public void doDelete(String title, int id) {

		final File clickedFile = new File(this.directoryEntries.getIcon(id)
				.getText());
		final File deleteFile = new File(this.currentDirectory.getPath()
				+ clickedFile.getPath());

		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(title);
		alert.setIcon(R.drawable.alert);
		alert.setMessage("Delete " + clickedFile.getName() + "?");

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (deleteFile.isDirectory()) {
					if (ShellInterface.isSuAvailable()) {
						ShellInterface.runCommand("rm -rf "
								+ deleteFile.getPath());
					} else {
						ShellInterface.setShell("sh");
						ShellInterface.runCommand("rm -rf "
								+ deleteFile.getPath());
					}
					Toast.makeText(getApplicationContext(),
							clickedFile.getName() + " has been deleted.",
							Toast.LENGTH_SHORT).show();

				} else {
					deleteFile.delete();
					Toast.makeText(getApplicationContext(),
							clickedFile.getName() + " has been deleted.",
							Toast.LENGTH_SHORT).show();
				}

				fill(deleteFile.getParentFile().listFiles());
			}
		});

		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();

	}

	public void doRename(int id) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Rename");
		alert.setIcon(R.drawable.info);

		final EditText input = new EditText(this);
		alert.setView(input);
		File clickedFile = new File(this.directoryEntries.getIcon(id).getText());
		final File path = new File(this.currentDirectory.getPath()
				+ clickedFile.getPath());
		input.setText(clickedFile.getName());

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString().trim();
				File folder = new File(path.getPath());
				File rename = new File(path.getParent() + "/" + value);
				if (!rename.exists()) {
					folder.renameTo(rename);
				} else {
					Toast.makeText(
							getApplicationContext(),
							"Error! Does the file " + "'" + value + "'"
									+ " already exist?", Toast.LENGTH_LONG)
							.show();
				}
				fill(folder.getParentFile().listFiles());
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		alert.show();
	}

	public void doDecompress(String title, String message) {

		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(title);
		alert.setIcon(R.drawable.info);
		alert.setMessage("Unzip " + this.currentDirectory.getName() + " ?");

		final String zipPath = this.currentDirectory.getPath();
		final String unZipPath = this.currentDirectory.getParent();
		final Decompress decompress = new Decompress(zipPath, unZipPath);

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				decompress.unzip();
			}
		});

		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();
	}

	public void doCreateFolder(String title) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(title);
		alert.setIcon(R.drawable.info);

		final EditText input = new EditText(this);
		alert.setView(input);
		final String path = this.currentDirectory.getPath();
		final File directory = this.currentDirectory;

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString().trim();
				File folder = new File(path + "/" + value);
				boolean success = false;
				if (!folder.exists()) {
					success = folder.mkdir();
				}
				if (!success) {
					Toast.makeText(
							getApplicationContext(),
							"Error! Does the folder " + "'" + value + "'"
									+ " already exist?", Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(
							getApplicationContext(),
							"The folder " + "'" + value + "'"
									+ " Has been created.", Toast.LENGTH_LONG)
							.show();
				}
				fill(directory.listFiles());
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		alert.show();
	}

	public void doCreateFile(String title) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(title);
		alert.setIcon(R.drawable.info);

		final EditText input = new EditText(this);
		alert.setView(input);
		final String path = this.currentDirectory.getPath();
		final File directory = this.currentDirectory;

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString().trim();
				File file = new File(path + "/" + value);
				boolean success = false;
				if (!file.exists()) {
					try {
						success = file.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (!success) {
					Toast.makeText(
							getApplicationContext(),
							"Error! Does the folder " + "'" + value + "'"
									+ " already exist?", Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(
							getApplicationContext(),
							"The folder " + "'" + value + "'"
									+ " Has been created.", Toast.LENGTH_LONG)
							.show();
				}
				fill(directory.listFiles());
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		alert.show();
	}

	public void doGetProperties(int id) {

		final File clickedFile = new File(this.directoryEntries.getIcon(id)
				.getText());
		final File propertiesFile = new File(this.currentDirectory.getPath()
				+ clickedFile.getPath());
		final String fileName = clickedFile.getName();

		// Get Timestamp
		final long timeStamp = propertiesFile.lastModified();
		Date d = new Date(timeStamp);
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
		String dateString = sdf.format(d);

		// Get Size of File or Dir
		long fileSize;
		if (propertiesFile.isDirectory()) {
			fileSize = 0;
		} else {
			fileSize = propertiesFile.length();
		}
		// long fileSize = getFolderSize(propertiesFile.listFiles());
		String fileSizeString = convertSizetoHuman(fileSize);

		// Get file or dir permissions
		String filePermissionsFinal = getFilePermissions(propertiesFile);

		// Get file or dir owner
		String fileOwner = getFileOwner(propertiesFile);

		// getIcon file or dir group
		String fileGroup = getFileGroup(propertiesFile);

		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Properties");
		alert.setIcon(R.drawable.info);
		alert.setMessage("Name: " + fileName + "\n" + "Size: " + fileSizeString
				+ "\n" + "Timestamp: " + dateString + "\n" + "Permissions: "
				+ filePermissionsFinal + "\n" + "Owner: " + fileOwner + "\n"
				+ "Group: " + fileGroup);

		alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();
	}

	public void doFlashZip(int id) {
		final File clickedFile = new File(this.directoryEntries.getIcon(id)
				.getText());
		final File flashFile = new File(this.currentDirectory.getPath()
				+ clickedFile.getPath());
		final String start = flashFile.getPath().substring(1, 4);
		String filePath = new String();
		if (start.equals("mnt")) {
			filePath = flashFile.getPath().substring(12);
		} else {
			filePath = flashFile.getPath().substring(8);
		}

		final String finalFilePath = filePath;

		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Flash Selected Zip File");
		alert.setIcon(R.drawable.alert);
		alert.setMessage("Flash " + clickedFile.getName()
				+ "?\n\n(Reboot Required)");

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (ShellInterface.isSuAvailable()) {
					ShellInterface.runCommand("rm -r /cache/recovery/command");
					ShellInterface.runCommand("mkdir -p /cache/recovery/");
					ShellInterface
							.runCommand("echo 'boot-recovery ' > /cache/recovery/command");
					// ShellInterface.runCommand("echo 'nandroid-mobile.sh -b --norecovery --nocache --nomisc --nosplash1 --nosplash2 --defaultinput 1>&2' >> /cache/recovery/command");
					// ShellInterface.runCommand("echo '--wipe_data' > /cache/recovery/command");
					// ShellInterface.runCommand("echo '--wipe_cache' > /cache/recovery/command");
					ShellInterface.runCommand("echo '--update_package=SDCARD:"
							+ finalFilePath + "'"
							+ ">> /cache/recovery/command");
					ShellInterface
							.runCommand("echo '--reboot' >> /cache/recovery/command");
					ShellInterface.runCommand("reboot recovery");
				}

			}
		});

		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();

	}

	public void doAddFlashZiptoList(int id) {
		final File clickedFile = new File(this.directoryEntries.getIcon(id)
				.getText());
		final File flashFile = new File(this.currentDirectory.getPath()
				+ clickedFile.getPath());

		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Add Zip File to List");
		alert.setIcon(R.drawable.alert);
		alert.setMessage("Add " + clickedFile.getName() + " to the Flash List?");

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				if (flashList.contains(flashFile)) {
					Toast.makeText(getApplicationContext(),
							"File Already in List", Toast.LENGTH_SHORT).show();
				} else {
					flashList.add(flashFile);
					Toast.makeText(getApplicationContext(),
							"File Added to List", Toast.LENGTH_SHORT).show();
				}

			}
		});

		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();

	}

	public void doFlashZipList() {

		String listFiles = "";
		int num;

		if (!flashList.isEmpty()) {

			for (int i = 0; i < flashList.size(); i++) {

				String start = flashList.getIcon(i).getPath().substring(1, 4);
				String filePath = new String();
				if (start.equals("mnt")) {
					filePath = flashList.getIcon(i).getPath().substring(12);
					File temp = new File(filePath);
					flashList.set(i, temp);
				} else {
					filePath = flashList.getIcon(i).getPath().substring(8);
					File temp = new File(filePath);
					flashList.set(i, temp);
				}
				num = i + 1;
				listFiles = listFiles.concat("(" + num + ") "
						+ flashList.getIcon(i).getName() + "\n\n");

				Log.d("***DEBUG***", flashList.getIcon(i).getPath());

			}

			final AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Flash Selected Zip Files");
			alert.setIcon(R.drawable.alert);
			alert.setMessage("Flash: \n\n" + listFiles + "(Reboot Required)");

			alert.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							if (ShellInterface.isSuAvailable()) {
								ShellInterface
										.runCommand("rm -r /cache/recovery/command");
								ShellInterface
										.runCommand("mkdir -p /cache/recovery/");
								ShellInterface
										.runCommand("echo 'boot-recovery ' > /cache/recovery/command");
								// ShellInterface.runCommand("echo 'nandroid-mobile.sh -b --norecovery --nocache --nomisc --nosplash1 --nosplash2 --defaultinput 1>&2' >> /cache/recovery/command");
								// ShellInterface.runCommand("echo '--wipe_data' > /cache/recovery/command");
								// ShellInterface.runCommand("echo '--wipe_cache' > /cache/recovery/command");
								for (int i = 0; i < flashList.size(); i++) {
									ShellInterface
											.runCommand("echo '--update_package=SDCARD:"
													+ flashList.getIcon(i)
															.getPath()
													+ "'"
													+ ">> /cache/recovery/command");
								}
								ShellInterface
										.runCommand("echo '--reboot' >> /cache/recovery/command");
								ShellInterface.runCommand("reboot recovery");
							}

						}
					});

			alert.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
		} else {
			Toast.makeText(getApplicationContext(), "List is Empty",
					Toast.LENGTH_SHORT).show();
		}

	}

	public void doClearZipFileList() {

		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Clear Flash List");
		alert.setIcon(R.drawable.alert);
		alert.setMessage("Are You Sure?");

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				flashList.clear();

			}
		});

		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();

	}

	public void doCopy(int id) {

		final File clickedFile = new File(this.directoryEntries.getIcon(id)
				.getText());
		File copyfile = new File(this.currentDirectory.getPath()
				+ clickedFile.getPath());

		copyFile = copyfile;

		Log.d("***DEBUG***", copyFile.getPath());

	}

	public void doPaste() {

		ProgressDialog MyDialog = ProgressDialog.show(FileBrowser.this, " ",
				" Loading. Please wait ... ", true);

		if (moveSelect == false) {

			if (copyFile.isDirectory()) {

				File folder = new File(this.currentDirectory + "/"
						+ copyFile.getName());

				if (!folder.exists()) {

					folder.mkdir();
				}

				if (ShellInterface.isSuAvailable()) {
					ShellInterface.runCommand("cp -r " + copyFile.getPath()
							+ "*//* " + folder);
				}
			} else {

				if (ShellInterface.isSuAvailable()) {
					ShellInterface.runCommand("cp " + copyFile.getPath() + " "
							+ this.currentDirectory);
				}
			}

		} else {

			File folder = new File(this.currentDirectory + "/"
					+ copyFile.getName());

			if (!folder.exists()) {

				if (ShellInterface.isSuAvailable()) {
					ShellInterface.runCommand("mv " + copyFile.getPath() + " "
							+ this.currentDirectory);
				}

			} else {

				if (ShellInterface.isSuAvailable()) {
					ShellInterface.runCommand("mv -f " + copyFile.getPath()
							+ " " + this.currentDirectory);
				}

			}

			File newDir = new File(this.currentDirectory + "/"
					+ copyFile.getName());
			copyFile = newDir;

			moveSelect = false;
		}

		Log.d("***DEBUG***", "cp -r " + copyFile.getPath() + "*//* "
				+ this.currentDirectory + "/" + copyFile.getName());

		doRefresh();

		MyDialog.dismiss();

	}

	public void doMove(int id) {

		doCopy(id);
		moveSelect = true;

	}

	public void doUnzip(int id) {

		final File clickedFile = new File(this.directoryEntries.getIcon(id)
				.getText());
		final File unzipFile = new File(this.currentDirectory.getPath()
				+ clickedFile.getPath());

		int folderLength = clickedFile.getName().length();
		final String folderName = clickedFile.getName().substring(0,
				folderLength - 4);

		final File path = new File(this.currentDirectory + "/" + folderName);

		Log.d("***DEBUG***", path.getPath());

		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Unzip File");
		alert.setIcon(R.drawable.alert);
		alert.setMessage("Unzip " + clickedFile.getName() + "?");

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				final Decompress decompress = new Decompress(unzipFile
						.getPath(), path.getPath());

				if (!path.exists()) {
					path.mkdir();

				}

				if (ShellInterface.isSuAvailable()) {
					ShellInterface.runCommand("mount -o remount,rw /");
					ShellInterface.runCommand("chmod a+rw "
							+ unzipFile.getParent());
					ShellInterface.runCommand("chmod a+rw "
							+ unzipFile.getPath());
					ShellInterface.runCommand("mount -o remount,ro /");
				}
				decompress.unzip();
				fill(path.getParentFile().listFiles());
			}
		});

		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();

	}

   public void doSend(int id)
   {

      final File clickedFile = new File(this.directoryEntries.getIcon(id).getText());
      final File sendFile = new File(this.currentDirectory.getPath()
            + clickedFile.getPath());

      String type = getMIMEType(sendFile);

      // need to "send multiple" to getIcon more than one attachment
      final Intent emailIntent = new Intent(Intent.ACTION_SEND);
      emailIntent.setType(type);

      emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse ("file://" + sendFile));
      startActivity(Intent.createChooser(emailIntent, "Choose Send Method"));

   }

	public void ReadZipFile() {

		String filename = this.currentDirectory.getPath();
		File zipFiles[] = new File[999];
		int i = 0;

		try {
			byte[] buf = new byte[1024];
			ZipInputStream zipinputstream = null;
			ZipEntry zipentry;
			zipinputstream = new ZipInputStream(new FileInputStream(filename));

			zipentry = zipinputstream.getNextEntry();
			while (zipentry != null) {
				// for each entry to be extracted
				String entryName = zipentry.getName();
				System.out.println("File ::" + entryName);
				RandomAccessFile rf;
				File newFile = new File(entryName);
				String directory = newFile.getParent();

				if (directory == null) {
					if (newFile.isDirectory())
						break;
				}

				rf = new RandomAccessFile(entryName, "r");
				String line;

				if ((line = rf.readLine()) != null) {
					System.out.println(line);
				}

				zipFiles[i] = newFile;

				rf.close();
				zipinputstream.closeEntry();
				zipentry = zipinputstream.getNextEntry();

			}// while

			zipinputstream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		fill(zipFiles);

	}

	public long getFolderSize(File[] directoryList) {
		long folderSize = 0;
		for (int i = 0; i < directoryList.length; i++) {
			File currentFile = directoryList[i];
			if (currentFile.isDirectory()) {
				// folderSize += getFolderSize(currentFile.listFiles());
				folderSize = 0;
			} else {
				folderSize += currentFile.length();
			}
		}
		return folderSize;
	}

	public String convertSizetoHuman(long fileSize) {
		// Convert to readable units
		DecimalFormat df = new DecimalFormat("#.##");
		String fileSizeString = new String();
		if (fileSize < 1024) {
			fileSizeString = (df.format(fileSize) + " Bytes");
		} else if (fileSize >= 1024 && fileSize < 1048576) {

			float size = (float) fileSize / 1024;
			fileSizeString = (df.format(size) + " KB");
		} else if (fileSize >= 1048576 && fileSize < 1073741824) {

			float size = (float) fileSize / 1048576;
			fileSizeString = (df.format(size) + " MB");
		} else if (fileSize >= 1073741824) {
			float size = (float) fileSize / 1073741824;
			fileSizeString = (df.format(size) + " GB");
		}

		return fileSizeString;
	}

	public String getFilePermissions(File propertiesFile) {
		String filePermissions = new String();
		if (propertiesFile.isDirectory()) {
			if (ShellInterface.isSuAvailable()) {
				filePermissions = ShellInterface.getProcessOutput("ls -ld "
						+ propertiesFile.getPath());
			}
		} else {
			if (ShellInterface.isSuAvailable()) {
				filePermissions = ShellInterface.getProcessOutput("ls -l "
						+ propertiesFile.getPath());
			}
		}
		String filePermissionsFinal = filePermissions.substring(1, 10);

		return filePermissionsFinal;
	}

	public String getFileOwner(File propertiesFile) {
		String filePermissions = new String();
		if (propertiesFile.isDirectory()) {
			if (ShellInterface.isSuAvailable()) {
				filePermissions = ShellInterface.getProcessOutput("ls -ld -n "
						+ propertiesFile.getPath());
			}
		} else {
			if (ShellInterface.isSuAvailable()) {
				filePermissions = ShellInterface.getProcessOutput("ls -l -n "
						+ propertiesFile.getPath());
			}
		}
		String fileOwner = filePermissions.substring(16, 20);
		fileOwner.trim();

		return fileOwner;
	}

	public String getFileGroup(File propertiesFile) {
		String filePermissions = new String();
		if (propertiesFile.isDirectory()) {
			if (ShellInterface.isSuAvailable()) {
				filePermissions = ShellInterface.getProcessOutput("ls -ld -n "
						+ propertiesFile.getPath());
			}
		} else {
			if (ShellInterface.isSuAvailable()) {
				filePermissions = ShellInterface.getProcessOutput("ls -l -n "
						+ propertiesFile.getPath());
			}
		}
		String fileGroup = filePermissions.substring(25, 30);
		fileGroup.trim();

		return fileGroup;
	}

	public void doMountRO() {
		
		if (ShellInterface.isSuAvailable()) {
			ShellInterface.runCommand("mount -o remount,rw /");
			ShellInterface.runCommand("chmod a+r /cache*//*");
			ShellInterface.runCommand("chmod a+r /config*//*");
			ShellInterface.runCommand("chmod a+r /sbin*//*");
			ShellInterface.runCommand("chmod a+r /root*//*");
			ShellInterface.runCommand("chmod a+r "
					+ this.currentDirectory.getPath());
			ShellInterface.runCommand("chmod a+rw /mnt/sdcard*//*");
			ShellInterface.runCommand("mount -o remount,ro /system");
			ShellInterface.runCommand("mount -o remount,rw /data");
			ShellInterface.runCommand("mount -o remount,ro /");
		}
		fill(this.currentDirectory.listFiles());
		Toast.makeText(getApplicationContext(), "Mounted as R/O",
				Toast.LENGTH_LONG).show();
	}

	public void doMountRW() {
		
		if (ShellInterface.isSuAvailable()) {
			ShellInterface.runCommand("mount -o remount,rw /");
			ShellInterface.runCommand("chmod a+rw /cache*//*");
			ShellInterface.runCommand("chmod a+rw /config*//*");
			ShellInterface.runCommand("chmod a+rw /sbin*//*");
			ShellInterface.runCommand("chmod a+rw /root*//*");
			ShellInterface.runCommand("chmod a+rw "
					+ this.currentDirectory.getPath());
			ShellInterface.runCommand("chmod a+rw /mnt/sdcard*//*");
			ShellInterface.runCommand("mount -o remount,rw /system");
			ShellInterface.runCommand("mount -o remount,rw /data");
		}
		fill(this.currentDirectory.listFiles());
		Toast.makeText(getApplicationContext(), "Mounted as R/W",
				Toast.LENGTH_LONG).show();
	}

	public void doRefresh() {
		fill(this.currentDirectory.listFiles());
	}

	public void onBackPressed() {
		upOneLevel();
		return;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Options");

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		int position = info.position;

		File clickedFile = new File(this.directoryEntries.getIcon(position)
				.getText());
		final File file = new File(this.currentDirectory.getPath()
				+ clickedFile.getPath());

		String end = file
				.getName()
				.substring(file.getName().lastIndexOf(".") + 1,
						file.getName().length()).toLowerCase();

		MenuInflater inflater = getMenuInflater();

		if (file.isDirectory()) {

			inflater.inflate(R.menu.manager_context_menu_folder, menu);
		} else {
			if (end.equals("zip")) {
				inflater.inflate(R.menu.manager_context_menu_zip, menu);
			} else {
				inflater.inflate(R.menu.manager_context_menu_file, menu);
			}
		}

	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.delete:
			doDelete("Delete", (int) info.id);
			return true;
		case R.id.rename:
			doRename((int) info.id);
			return true;
		case R.id.properties:
			doGetProperties((int) info.id);
			return true;
		case R.id.flash_zip:
			doFlashZip((int) info.id);
			return true;
		case R.id.unzip_file:
			doUnzip((int) info.id);
			return true;
		case R.id.flashList:
			doAddFlashZiptoList((int) info.id);
			return true;
		case R.id.copy:
			doCopy((int) info.id);
			return true;
		case R.id.move:
			doMove((int) info.id);
			return true;
		case R.id.send:
           doSend((int) info.id);
           return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	*//**
	 * Checks whether checkItsEnd ends with one of the Strings from fileEndings
	 *//*
	private boolean checkEndsWithInStringArray(String checkItsEnd,
			String[] fileEndings) {
		for (String aEnd : fileEndings) {
			if (checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}

	// Create Main Menu
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.file_manager, menu);
		return true;
	}

	// Define Main Menu Options
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.home) {
			browseToRoot();
		} else if (id == R.id.sdcard) {
			browseToSdcard();
		} else if (id == R.id.new_folder) {
			doCreateFolder("Enter Folder Name");
		} else if (id == R.id.mount_ro) {
			doMountRO();
		} else if (id == R.id.mount_rw) {
			doMountRW();
		} else if (id == R.id.new_file) {
			doCreateFile("Enter File Name");
		} else if (id == R.id.refresh) {
			doRefresh();
		} else if (id == R.id.manager_preferences) {
			// doDocumentKeys();
		} else if (id == R.id.flashzipfiles) {
			doFlashZipList();
		} else if (id == R.id.clearFlashList) {
			doClearZipFileList();
		} else if (id == R.id.paste) {
			doPaste();
		}
		return super.onOptionsItemSelected(item);
	}*/
}